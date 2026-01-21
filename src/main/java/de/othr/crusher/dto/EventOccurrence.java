package de.othr.crusher.dto;

import de.othr.crusher.model.EventEntity;

import java.time.LocalDate;

public record EventOccurrence(EventEntity event, LocalDate date) {}
