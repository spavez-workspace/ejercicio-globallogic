package com.globallogic.ejercicio.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Phone {

	@Id
    @GeneratedValue
    private Long id;

    private Long number;
    private int citycode;
    private String contrycode;
	
    @ManyToOne
    @JoinColumn(name = "user_id")
    @JsonIgnore
    private UserExample user;
}
