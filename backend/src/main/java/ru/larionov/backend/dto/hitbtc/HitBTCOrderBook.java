package ru.larionov.backend.dto.hitbtc;

import lombok.Data;

import java.util.ArrayList;

@Data
public class HitBTCOrderBook {
    private String timestamp;
    private ArrayList<ArrayList<String>> ask;
    private ArrayList<ArrayList<String>> bid;
}
