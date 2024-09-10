package org.boot.dontspike.OpenAI;

import org.boot.dontspike.DTO.FoodDetailDto;
import org.boot.dontspike.Food.Food;
import org.boot.dontspike.Food.FoodRepository;
import org.boot.dontspike.FoodWiki.FoodWikiRepository;
import org.boot.dontspike.FoodWiki.Foodwiki;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.regex.Matcher;


@Service
public class gptService {

    private static final Logger logger = LoggerFactory.getLogger(gptService.class);

    @Value("${shortweather-key}")
    private String apiKey;

    private final RestTemplate restTemplate = new RestTemplate();
    private final FoodWikiRepository foodWikiRepository;
    private final FoodRepository foodRepository;


    public gptService(FoodWikiRepository foodWikiRepository, FoodRepository foodRepository) {
        this.foodWikiRepository = foodWikiRepository;
        this.foodRepository = foodRepository;
    }

    public FoodDetailDto getFoodDetails(String foodName) {
        String apiUrl = "https://api.openai.com/v1/chat/completions";
        String prompt = String.format(
                "음식 항목 %s에 대한 상세 정보를 자세히 제공해 주세요. 양, 열량, 탄수화물, 단백질, 지방, 나트륨, 콜레스테롤은 단위(g,mg 등)없이 숫자로만 출력해주세요 포함할 내용: " +
                        "양(int, g 기준으로, g만 출력해주세요), 열량(double), 탄수화물(double), 단백질(double), 지방(double), " +
                        "나트륨(double), 콜레스테롤(double), 전문가의 소견(string), " +
                        "적정 섭취량(string), 섭취 방법(string), 혈당 지수(string).",
                foodName
        );

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + apiKey);
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> body = new HashMap<>();
        body.put("model", "gpt-3.5-turbo");
        body.put("messages", List.of(
                Map.of("role", "user", "content", prompt)
        ));
        body.put("max_tokens", 500);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

        // API 호출
        ResponseEntity<Map> response = restTemplate.postForEntity(apiUrl, request, Map.class);
        Map<String, Object> responseBody = response.getBody();

        logger.info("API Response: {}", responseBody);

        if (responseBody != null) {
            Object choicesObj = responseBody.get("choices");
            if (choicesObj instanceof List) {
                List<?> choicesList = (List<?>) choicesObj;
                if (!choicesList.isEmpty() && choicesList.get(0) instanceof Map) {
                    Map<?, ?> firstChoice = (Map<?, ?>) choicesList.get(0);
                    Object messageObj = firstChoice.get("message");
                    if (messageObj instanceof Map) {
                        Map<?, ?> messageMap = (Map<?, ?>) messageObj;
                        Object contentObj = messageMap.get("content");
                        if (contentObj != null && !contentObj.toString().contains("Hello!")) {
                            String result = contentObj.toString().trim();
                            FoodDetailDto dto = parseFoodDetails(result);
                            dto.setFoodname(foodName);// 검색한 음식 이름 설정

                            saveFoodDetailsToDB(dto);

                            return dto;
                        } else {
                            // 기본 값 반환 또는 재시도 로직
                            logger.warn("Unexpected response received: {}", contentObj);
                            return new FoodDetailDto();
                        }
                    }
                }
            }
        }

        return new FoodDetailDto();
    }


    private void saveFoodDetailsToDB(FoodDetailDto dto) {
        // Food 엔티티 생성 및 저장
        Food food = new Food();
        food.setFoodname(dto.getFoodname());
        food.setAmount(dto.getAmount());
        food.setCalorie(dto.getCalorie());
        food.setProtein(dto.getProtein());
        food.setFat(dto.getFat());
        food.setSodium(dto.getSodium());
        food.setCholesterol(dto.getCholesterol());
        food.setCarbohydrate(dto.getCarbohydrate());

        // 먼저 저장하여 food_id를 생성
        Food savedFood = foodRepository.save(food);

        // Foodwiki 엔티티 생성 및 저장
        Foodwiki foodwiki = new Foodwiki();
        foodwiki.setFooddataId(savedFood.getFooddata_id());  // 새로 생성된 food의 ID를 사용
        foodwiki.setExpertOpinion(dto.getExpertOpinion());
        foodwiki.setProperIntake(dto.getProperIntake());
        foodwiki.setIngestionMethod(dto.getIngestionMethod());
        foodwiki.setGi(dto.getGI());

        // Foodwiki 엔티티 저장
        foodWikiRepository.save(foodwiki);
    }

    private FoodDetailDto parseFoodDetails(String response) {
        String[] lines = response.split("\n");
        FoodDetailDto dto = new FoodDetailDto();

        for (String line : lines) {
            if (line.contains(":")) {
                String[] parts = line.split(":", 2);
                if (parts.length == 2) {
                    String key = parts[0].trim();
                    String value = parts[1].trim().replace("\"", ""); // 큰따옴표 제거

                    switch (key) {
                        case "양":
                        case "amount":
                            dto.setAmount(parseInt(value));
                            break;
                        case "열량":
                        case "calorie":
                            dto.setCalorie(parseDouble(value));
                            break;
                        case "탄수화물":
                        case "carbohydrate":
                            dto.setCarbohydrate(parseDouble(value));
                            break;
                        case "단백질":
                        case "protein":
                            dto.setProtein(parseDouble(value));
                            break;
                        case "지방":
                        case "fat":
                            dto.setFat(parseDouble(value));
                            break;
                        case "나트륨":
                        case "sodium":
                            dto.setSodium(parseDouble(value));
                            break;
                        case "콜레스테롤":
                        case "cholesterol":
                            dto.setCholesterol(parseDouble(value));
                            break;
                        case "전문가의 소견":
                        case "expert opinion":
                            dto.setExpertOpinion(value);
                            break;
                        case "적정 섭취량":
                        case "proper intake":
                            dto.setProperIntake(value);
                            break;
                        case "섭취 방법":
                        case "ingestion method":
                            dto.setIngestionMethod(value);
                            break;
                        case "혈당 지수":
                        case "gi":
                            dto.setGI(value);
                            break;
                        default:
                            // 해당하는 키가 없으면 무시
                            break;
                    }
                }
            }
        }

        return dto;
    }

    private int parseInt(String value) {
        try {
            return Integer.parseInt(value.replaceAll("[^0-9]", ""));
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    private double parseDouble(String value) {
        try {
            return Double.parseDouble(value.replaceAll("[^0-9.]", ""));
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }
}

