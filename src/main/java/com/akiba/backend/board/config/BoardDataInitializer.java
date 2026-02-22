package com.akiba.backend.board.config;

import com.akiba.backend.board.domain.Board;
import com.akiba.backend.board.domain.BoardCode;
import com.akiba.backend.board.repository.BoardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BoardDataInitializer implements ApplicationRunner {

    private final BoardRepository boardRepository;

    @Override
    public void run(ApplicationArguments args) {
        for (BoardCode boardCode : BoardCode.values()) {
            boardRepository.findByCode(boardCode)
                    .orElseGet(() -> boardRepository.save(Board.builder()
                            .code(boardCode)
                            .name(boardCode.getDisplayName())
                            .description(boardCode.getDefaultDescription())
                            .build()));
        }
    }
}
