package ru.bomber.core.mqtt;

public class Topics {
    public static final String ON_LOCK_TOPIC = "myhome/access_card_scanners/log";
    public static final String READ_NEW_TOPIC = "myhome/outdoors/wicket/read_new_card";

    public static final String TEMPERATURE_MESSAGE = "myhome/+/temperature";
    public static final String HUMIDITY_MESSAGE = "myhome/+/humidity";
    public static final String PRESSURE_MESSAGE = "myhome/+/pressure";
    public static final String COMMAND_TOPIC = "myhome/command/+";

    public static final String TRADER_LOG = "myhome/trader/logs";
}

