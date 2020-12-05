package com.changgou.service;

public interface ElasticMannagerService {
    void deleteIndexAndMapping();

    void createIndexAndMapping();


    void importAll();
}
