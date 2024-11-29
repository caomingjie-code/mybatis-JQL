package com.javaoffers.base.modelhelper.sample.sharding;

import com.javaoffers.base.modelhelper.sample.speedier.BriefSpeedierSample;
import com.javaoffers.brief.modelhelper.mapper.BriefMapper;
import com.javaoffers.brief.modelhelper.speedier.BriefSpeedier;
import org.junit.Test;

import java.util.Date;

public class ShardingSample {

    @Test
    public void testShardingSample(){
        BriefSpeedier briefSpeedier = BriefSpeedierSample.getBriefSpeedier();
        BriefMapper<ShardingUser> userBriefMapper = briefSpeedier.newDefaultBriefMapper(ShardingUser.class);
        userBriefMapper.select().colAll().where()
                .notIn(ShardingUser::getBirthday, new Date())
                .notIn(ShardingUser::getId,1)
                .exs();
    }
}
