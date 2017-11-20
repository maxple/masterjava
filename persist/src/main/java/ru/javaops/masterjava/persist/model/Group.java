package ru.javaops.masterjava.persist.model;

import lombok.*;

@Data
@RequiredArgsConstructor
@EqualsAndHashCode
@NoArgsConstructor
public class Group {
    private @NonNull
    String name;

    //    @Column("gr_type")
    private @NonNull
    GroupType gr_type;

    private @NonNull
    Project project;
}
