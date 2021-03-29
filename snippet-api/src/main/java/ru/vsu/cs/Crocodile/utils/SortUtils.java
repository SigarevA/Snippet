package ru.vsu.cs.Crocodile.utils;

import org.springframework.stereotype.Component;
import ru.vsu.cs.Crocodile.DTO.SnippetDTO;

import java.util.Collection;
import java.util.List;

@Component
public class SortUtils {

    public Collection<SnippetDTO> sortingSnippetDTOs(List<SnippetDTO> snippetDTOS) {
        snippetDTOS.sort(
            (s1, s2) ->
                s1.getDatePublication().before(s2.getDatePublication()) ? 1 : -1
        );
        return snippetDTOS;
    }
}