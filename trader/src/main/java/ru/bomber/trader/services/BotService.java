package ru.bomber.trader.services;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import ru.bomber.core.http.Response;
import ru.bomber.core.trader.models.BotDTO;
import ru.bomber.trader.Specifications.BotSpecifications;
import ru.bomber.trader.converter.BotConverter;
import ru.bomber.trader.models.Bot;
import ru.bomber.trader.reposytory.BotRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BotService {

    private final BotRepository botRepository;

    public List<BotDTO> getListBots(String namePart) {
        Specification<Bot> spec = Specification.where(null);

        if (!namePart.equals("")) {
            spec = spec.and(BotSpecifications.nameLike(namePart));
        }
        return botRepository
                .findAll(spec, Sort.by("name")).stream()
                .map(BotConverter::toDTO)
                .collect(Collectors.toList());
    }

    public Response saveBot(BotDTO botDTO) {
        botRepository.save(BotConverter.fromDTO(botDTO));
        return new Response(LocalDateTime.now(), "OK");
    }
}
