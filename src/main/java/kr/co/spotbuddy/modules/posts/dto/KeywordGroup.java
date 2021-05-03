package kr.co.spotbuddy.modules.posts.dto;

import lombok.Getter;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Getter
public enum KeywordGroup {

    LOCATION("지역", Arrays.asList(KEYWORDS.SEOUL, KEYWORDS.BUSAN, KEYWORDS.DAEGU, KEYWORDS.DAEJEON, KEYWORDS.GWANGJU,
            KEYWORDS.SUWON, KEYWORDS.CHANGWON, KEYWORDS.INCHEON)),
    TEAM("구단", Arrays.asList(KEYWORDS.DOOSAN, KEYWORDS.KIWOOM, KEYWORDS.LG, KEYWORDS.LOTTE, KEYWORDS.HANHWA, KEYWORDS.KIA,
            KEYWORDS.KT, KEYWORDS.NC, KEYWORDS.SSG)),
    EMPTY("없음", Collections.EMPTY_LIST);

    private final String keyword;
    private final List<KEYWORDS> keywordList;

    KeywordGroup(String keyword, List<KEYWORDS> keywordList) {
        this.keyword = keyword;
        this.keywordList = keywordList;
    }

    public static KeywordGroup findByKeyword(KEYWORDS keyword) {
        return Arrays.stream(KeywordGroup.values())
                .filter(keywordGroup -> keywordGroup.hasKeyword(keyword))
                .findAny()
                .orElse(EMPTY);
    }

    public boolean hasKeyword(KEYWORDS keyword) {
        return keywordList.stream()
                .anyMatch(word -> word == keyword);
    }



}
