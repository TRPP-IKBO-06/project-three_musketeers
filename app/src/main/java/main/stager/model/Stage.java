package main.stager.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
public class Stage extends FBModel {
    @Getter private Status currentStatus;
    @Getter private String name;
    @Getter private TriggerType trigger;
}