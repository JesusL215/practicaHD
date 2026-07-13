package com.smartpark.backend.model.domain;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.NoArgsConstructor;

@Entity
@DiscriminatorValue("AUTO")
@NoArgsConstructor
public class Auto extends Vehiculo {

}