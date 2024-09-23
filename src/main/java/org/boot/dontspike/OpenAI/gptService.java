package org.boot.dontspike.OpenAI;

import org.boot.dontspike.BloodSugar.BloodSugarAnalysisDto;
import org.boot.dontspike.BloodSugar.BloodSugarService;
import org.boot.dontspike.DTO.FoodDetailDto;
import org.boot.dontspike.DTO.FrequentFoodDto;
import org.boot.dontspike.Food.Food;
import org.boot.dontspike.Food.FoodRepository;
import org.boot.dontspike.Food.FoodService;
import org.boot.dontspike.Food.FrequentAnalysisDto;
import org.boot.dontspike.FoodWiki.FoodWikiRepository;
import org.boot.dontspike.FoodWiki.Foodwiki;
import org.boot.dontspike.JWT.JWTUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.client.RestTemplate;
import java.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class gptService {

    private static final Logger logger = LoggerFactory.getLogger(gptService.class);

    @Value("${shortweather-key}")
    private String apiKey;

    private final RestTemplate restTemplate = new RestTemplate();
    private final FoodWikiRepository foodWikiRepository;
    private final FoodRepository foodRepository;
    private final BloodSugarService bloodSugarService;
    private final FoodService foodService;
    private final JWTUtil jwtUtil;


    public gptService(FoodWikiRepository foodWikiRepository, FoodRepository foodRepository,  BloodSugarService bloodSugarService, FoodService foodService,JWTUtil jwtUtil) {
        this.foodWikiRepository = foodWikiRepository;
        this.foodRepository = foodRepository;
        this.bloodSugarService = bloodSugarService;
        this.jwtUtil = jwtUtil;
        this.foodService = foodService;
    }



    //자주 먹은 음식 리스트 가져와서 분석하는 코드
    public FrequentAnalysisDto getFrequentAnalysis(String username, LocalDateTime startDate, LocalDateTime endDate) {
        // 최근 30일 동안 자주 섭취한 음식 가져오기
        List<FrequentFoodDto> frequentFood = foodService.getFoodsEatenAtLeastFiveTimesInMonth(username, startDate, endDate);

        // 프롬프트 생성
        String prompt = createFrequentFoodAnalysisPrompt(frequentFood, startDate.getYear(), startDate.getMonth().toString());

        String apiUrl = "https://api.openai.com/v1/chat/completions";

        Map<String, Object> responseBody=gpt(apiUrl,prompt);

        // 응답에서 분석 결과 추출
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
                            FrequentAnalysisDto dto = parseFrequentAnalysis(result);

                            return dto;
                        } else {
                            logger.warn("Unexpected response received: {}", contentObj);
                            return new FrequentAnalysisDto();
                        }
                    }
                }
            }
        }

        return new FrequentAnalysisDto();
    }

    //월별 공복 혈당 분석해서 코맨트 가져오는 코드
    public BloodSugarAnalysisDto getMonthlyBloodSugarAnalysis(@RequestHeader("Authorization") String token, int year) {

        // JWT 토큰에서 "Bearer " 부분 제거
        String tokenValue = token.replace("Bearer ", "");

        // JWT 토큰에서 user_id를 추출 (JWTUtil을 사용하여 처리)
        String username = jwtUtil.getUsername(tokenValue);
        // 데이터베이스에서 사용자의 월별 혈당 데이터를 가져옴
        Map<String, Double> monthlyAverages = bloodSugarService.getMonthlyAverages(username, year);

        // 월별 혈당 평균 값을 GPT API로 분석 요청
        String apiUrl = "https://api.openai.com/v1/chat/completions";
        String prompt = createBloodSugarAnalysisPrompt(monthlyAverages, year);

        Map<String, Object> responseBody=gpt(apiUrl, prompt);


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
                            BloodSugarAnalysisDto dto = parseBloodSugarAnalysis(result);

                            return dto;
                        } else {
                            logger.warn("Unexpected response received: {}", contentObj);
                            return new BloodSugarAnalysisDto();
                        }
                    }
                }
            }
        }

        return new BloodSugarAnalysisDto();
    }

    // 자주 섭취한 음식 데이터를 기반으로 프롬프트 생성
    private String createFrequentFoodAnalysisPrompt(List<FrequentFoodDto> frequentFood, int year, String month) {
        StringBuilder prompt = new StringBuilder();

        // 프롬프트 기본 정보 추가
        prompt.append(String.format("다음은 %d년 %s 동안 사용자가 자주 섭취한 음식 목록입니다. ", year, month));
        prompt.append("이 목록을 바탕으로 사용자의 식습관에 대해 분석하고 혈당관리를 개선할 수 있는 팁을 제공해 주세요.(500자 이내로)\n\n");

        // 음식 목록 추가
        prompt.append("자주 섭취한 음식 목록:\n");
        for (FrequentFoodDto food : frequentFood) {
            prompt.append(String.format("- %s: %d회 섭취\n", food.getFoodName(), food.getCount()));
        }

        // 분석 요청 추가
        prompt.append("자주 섭취한 음식 목록을 분석해서 주로 어떤걸 먹었는지 알려주세요.");
        prompt.append("자주 섭취한 음식을 보고, 식습관에 대한 개선 팁을 제시해주세요");

//        prompt.append("1. 사용자의 식습관 분석\n");
//        prompt.append("2. 섭취 빈도가 높은 음식에 대한 건강 분석\n");
//        prompt.append("3. 건강한 식습관을 위한 개선 팁 제시\n");

        return prompt.toString();
    }
    // GPT에게 보낼 프롬프트 생성 (월별 혈당 데이터를 기반으로 분석 요청)
    private String createBloodSugarAnalysisPrompt(Map<String, Double> monthlyAverages, int year) {
        StringBuilder prompt = new StringBuilder();
        prompt.append(String.format("다음은 %d년 사용자의 월별 공복 혈당 수치입니다. 월별 공복 혈당 수치를 비교하여 변화와 분석을 제공해 주세요.(500자 이내로)\n", year));
        prompt.append("월별 공복 혈당 수치:\n");

        for (Map.Entry<String, Double> entry : monthlyAverages.entrySet()) {
            prompt.append(String.format("%s: %.2f mg/dl\n", entry.getKey(), entry.getValue()));
        }

        prompt.append("\n분석 결과와 권장 사항을 제공해 주세요(혈당 수치가 0인 달은 분석하지 말아주세요): \n");
        prompt.append("1. 혈당 수치 변화에 대한 설명\n");
        prompt.append("2. 지난달 대비 혈당이 증가한 경우 생활습관 점검 및 전문가 상담 권장\n");
        prompt.append("3. 건강 유지 위한 작은 변화와 실천 팁\n");

        return prompt.toString();
    }

    // GPT로부터 받은 응답을 DTO로 파싱 - 월별 공복 혈당
    private BloodSugarAnalysisDto parseBloodSugarAnalysis(String response) {
        BloodSugarAnalysisDto dto = new BloodSugarAnalysisDto();
        dto.setAnalysis(response);
        return dto;
    }
    //gpt로부터 받은 응답 DTO로 파싱 - 자주 먹은 음식 분석
    private FrequentAnalysisDto parseFrequentAnalysis(String response) {
        FrequentAnalysisDto dto = new FrequentAnalysisDto();
        dto.setAnalysis(response);
        return dto;
    }

    //gpt api 호출 시 중복 처리
    public Map<String, Object> gpt(String apiUrl, String prompt){
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
        return responseBody;
    }
    private FoodDetailDto mapToDto(Food food, Foodwiki foodwiki) {
        FoodDetailDto dto = new FoodDetailDto();
        dto.setFoodname(food.getFoodname());
        dto.setAmount(food.getAmount());
        dto.setCalorie(food.getCalorie());
        dto.setProtein(food.getProtein());
        dto.setFat(food.getFat());
        dto.setSodium(food.getSodium());
        dto.setCholesterol(food.getCholesterol());
        dto.setCarbohydrate(food.getCarbohydrate());
        dto.setExpertOpinion(foodwiki.getExpertOpinion());
        dto.setProperIntake(foodwiki.getProperIntake());
        dto.setIngestionMethod(foodwiki.getIngestionMethod());
        dto.setGI(foodwiki.getGi());
        return dto;
    }

    //foodwiki 내용 받아오는 코드
    public FoodDetailDto getFoodDetails(String foodName) {
        long startTime = System.currentTimeMillis();  // 시작 시간 기록
        Optional<Food> food = foodRepository.findByFoodname(foodName);
        if (food.isPresent()) {
            // 이미 음식 정보가 DB에 있을 경우, 해당 정보를 DTO로 변환하여 반환
            Foodwiki foodwiki = foodWikiRepository.findByFood(food.orElse(null));
            if (foodwiki != null) {
                long endTime = System.currentTimeMillis();  // 로컬 DB에서 반환하는 시간 기록
                logger.info("Local DB 조회 및 반환 시간: {} ms", (endTime - startTime));
                return mapToDto(food.orElse(null), foodwiki);
            }
        }
        // GPT API 호출 시작 시간 기록
        long gptStartTime = System.currentTimeMillis();

        String apiUrl = "https://api.openai.com/v1/chat/completions";
        String prompt = String.format(
                "음식 항목 %s에 대한 상세 정보를 자세히 제공해 주세요.(500자 이내로) 양, 열량, 탄수화물, 단백질, 지방, 나트륨, 콜레스테롤은 단위(g,mg 등)없이 숫자로만 출력해주세요 포함할 내용: " +
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

        long gptEndTime = System.currentTimeMillis();  // GPT API 응답 시간 기록
        logger.info("GPT API 호출 및 응답 시간: {} ms", (gptEndTime - gptStartTime));
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

                            // GPT 응답 파싱 시간 기록
                            long parseEndTime = System.currentTimeMillis();
                            logger.info("GPT 응답 파싱 시간: {} ms", (parseEndTime - gptEndTime));

                            // DB 저장 시작 시간 기록
                            long dbSaveStartTime = System.currentTimeMillis();
                            saveFoodDetailsToDB(dto);
                            long dbSaveEndTime = System.currentTimeMillis();  // DB 저장 완료 시간 기록

                            logger.info("DB 저장 시간: {} ms", (dbSaveEndTime - dbSaveStartTime));

                            // 전체 프로세스 시간 기록
                            long totalEndTime = System.currentTimeMillis();
                            logger.info("전체 요청부터 DB 저장까지 소요된 시간: {} ms", (totalEndTime - startTime));

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
        logger.info("Saving food details: name={}, amount={}, calorie={}, protein={}",
                dto.getFoodname(), dto.getAmount(), dto.getCalorie(), dto.getProtein());
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



    //FoodDetail 변환
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
            logger.error("Failed to parse integer from value: {}", value, e);
            return 0;
        }
    }

    private double parseDouble(String value) {
        try {
            return Double.parseDouble(value.replaceAll("[^0-9.]", ""));
        } catch (NumberFormatException e) {
            logger.error("Failed to parse double from value: {}", value, e);
            return 0.0;  // 예외 발생 시 0.0 반환
        }
    }

}
