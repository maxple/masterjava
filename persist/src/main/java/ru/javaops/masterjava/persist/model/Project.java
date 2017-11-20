package ru.javaops.masterjava.persist.model;

import lombok.*;

@Data
@RequiredArgsConstructor
@EqualsAndHashCode
@NoArgsConstructor
public class Project {
    private @NonNull
    String name;
    private @NonNull
    String description;
}
