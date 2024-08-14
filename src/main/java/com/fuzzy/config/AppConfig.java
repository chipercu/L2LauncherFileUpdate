package com.fuzzy.config;

import com.fuzzy.config.configsystem.Config;
import com.fuzzy.config.configsystem.ISubsystemConfig;

public class AppConfig implements ISubsystemConfig {

    @Override
    public String getConfigPathName() {
        return "app_config";
    }

    @Override
    public void init(){
        System.out.println(getConfigPathName() + " - leaded");
    }

    @Config(desc = "Путь к папке клиента Lineage 2",
            strValue = "") public static String CLIENT_DIRECTORY;
    @Config(desc = "Директория data",
            strValue = "data/") public static String DATA_DIRECTORY;
    @Config(desc = "Директория временных файлов",
            strValue = "data/temp/") public static String TEMP_DIRECTORY;
    @Config(desc = "Директория конфиг файлов",
            strValue = "data/config/") public static String CONFIG_DIRECTORY = "data/config/";

    @Config(desc = "Последний выбранный фильтр по расширению файла",
            strValue = "") public static String LAST_EXTENSION_FILTER;

    @Config(desc = "Последний выбранный фильтр по имени файла",
            strValue = "") public static String LAST_FILE_NAME_FILTER;



}