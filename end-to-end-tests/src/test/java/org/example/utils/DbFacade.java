package org.example.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestComponent;
import org.springframework.jdbc.core.JdbcTemplate;

@TestComponent
public class DbFacade {


    @Autowired(required = false)
    JdbcTemplate jdbcTemplate;

    public Integer checkNameInserted(String name){
        Integer resultsCount = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM client where name=?",Integer.class, name);
        return resultsCount;
    }
}
