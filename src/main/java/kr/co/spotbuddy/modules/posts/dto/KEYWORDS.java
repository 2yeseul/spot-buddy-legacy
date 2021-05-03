package kr.co.spotbuddy.modules.posts.dto;

import lombok.Getter;

@Getter
public enum KEYWORDS {

    SEOUL("서울"),
    INCHEON("인천"),
    SUWON("수원"),
    DAEJEON("대전"),
    GWANGJU("광주"),
    BUSAN("부산"),
    DAEGU("대구"),
    CHANGWON("창원"),
    DOOSAN("두산 베어스"),
    KIWOOM("키움 히어로즈"),
    LG("LG 트윈스"),
    LOTTE("롯데 자이언츠"),
    SAMSUNG("삼성 라이온즈"),
    HANHWA("한화 이글스"),
    KIA("KIA 타이거즈"),
    KT("kt wiz"),
    NC("NC 다이노스"),
    SSG("SSG 랜더스");

    private final String keywords;

    KEYWORDS(String keywords) {
        this.keywords = keywords;
    }
}
