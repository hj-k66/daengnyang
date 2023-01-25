package com.daengnyangffojjak.dailydaengnyang.domain.entity;

import com.daengnyangffojjak.dailydaengnyang.domain.dto.pet.PetAddRequest;
import com.daengnyangffojjak.dailydaengnyang.domain.entity.enums.Sex;
import com.daengnyangffojjak.dailydaengnyang.domain.entity.enums.Species;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Getter
public class Pet extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id")
    private Group group;
    @Enumerated(EnumType.STRING)
    private Species species;
    private String breed;       //추후 enum으로 수정 예정
    private String name;
    @Enumerated(EnumType.STRING)
    private Sex sex;
    private LocalDateTime birthday;
    private double weight;

    // pet 수정
    public void update (PetAddRequest petAddRequest){
        this.name = petAddRequest.getName();
        this.species = petAddRequest.getSpecies();
        this.breed = petAddRequest.getBreed();
        this.sex = petAddRequest.getSex();
        this.birthday = petAddRequest.getBirthday();
        this.weight = petAddRequest.getWeight();
    }
}
