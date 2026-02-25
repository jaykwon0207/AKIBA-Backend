package com.akiba.backend.board.domain;

public enum BoardCode {
    FREE("자유게시판", "자유롭게 소통하는 게시판"),
    AUTHENTICITY("정품감정 게시판", "정품/가품 감정을 위한 게시판"),
    QNA_HELP("질문/도움 게시판", "질문과 도움을 주고받는 게시판");

    private final String displayName;
    private final String defaultDescription;

    BoardCode(String displayName, String defaultDescription) {
        this.displayName = displayName;
        this.defaultDescription = defaultDescription;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDefaultDescription() {
        return defaultDescription;
    }
}
