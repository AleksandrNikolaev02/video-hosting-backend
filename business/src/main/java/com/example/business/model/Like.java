package com.example.business.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("like")
public class Like extends Reaction {
}
