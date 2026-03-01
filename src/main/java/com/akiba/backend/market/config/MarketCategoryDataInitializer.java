package com.akiba.backend.market.config;

import com.akiba.backend.used.domain.MarketCategory;
import com.akiba.backend.used.repository.MarketCategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class MarketCategoryDataInitializer implements ApplicationRunner {

    private final MarketCategoryRepository marketCategoryRepository;

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        if (marketCategoryRepository.count() > 0) {
            return;
        }

        // parent categories
        MarketCategory figure = marketCategoryRepository.save(MarketCategory.builder()
                .name("피규어")
                .sortOrder(1)
                .build());

        MarketCategory acrylic = marketCategoryRepository.save(MarketCategory.builder()
                .name("아크릴")
                .sortOrder(2)
                .build());

        MarketCategory plush = marketCategoryRepository.save(MarketCategory.builder()
                .name("인형")
                .sortOrder(3)
                .build());

        // child categories
        marketCategoryRepository.save(MarketCategory.builder()
                .parentId(figure.getCategoryId())
                .name("스케일 피규어")
                .sortOrder(1)
                .build());

        marketCategoryRepository.save(MarketCategory.builder()
                .parentId(figure.getCategoryId())
                .name("넨도로이드")
                .sortOrder(2)
                .build());

        marketCategoryRepository.save(MarketCategory.builder()
                .parentId(acrylic.getCategoryId())
                .name("아크릴 스탠드")
                .sortOrder(1)
                .build());

        marketCategoryRepository.save(MarketCategory.builder()
                .parentId(plush.getCategoryId())
                .name("봉제 인형")
                .sortOrder(1)
                .build());
    }
}

