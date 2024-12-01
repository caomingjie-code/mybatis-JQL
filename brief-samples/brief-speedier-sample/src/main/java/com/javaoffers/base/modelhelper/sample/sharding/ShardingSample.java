package com.javaoffers.base.modelhelper.sample.sharding;

import com.javaoffers.base.modelhelper.sample.speedier.BriefSpeedierSample;
import com.javaoffers.brief.modelhelper.mapper.BriefMapper;
import com.javaoffers.brief.modelhelper.speedier.BriefSpeedier;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.junit.Test;

import java.util.Date;
import java.util.List;

public class ShardingSample {
    BriefSpeedier briefSpeedier = BriefSpeedierSample.getBriefSpeedier();
    BriefMapper<ShardingUser> userBriefMapper = briefSpeedier.newDefaultBriefMapper(ShardingUser.class);

    @Test
    public void testShardingSampleEq(){
        userBriefMapper.select().colAll().where()
                .eq(ShardingUser::getBirthday, new Date())
                .eq(ShardingUser::getId,1)
                .exs();
    }

    @Test
    public void testShardingSampleIn(){
        List<ShardingUser> exs = userBriefMapper.select().colAll().where()
                .in(ShardingUser::getBirthday, new Date(), new Date())
//                .eq(ShardingUser::getId, 1)
                .exs();
        System.out.println(exs.size());
    }

    @Test
    public void testShardingSampleIn2(){
        List<ShardingUser> exs = userBriefMapper.select().colAll().where()
                .in(ShardingUser::getBirthday, new Date(), DateUtils.addDays(new Date(), -31))
                .exs();
        System.out.println(exs.size());
    }

    @Test
    public void testShardingSampleGt(){
        List<ShardingUser> exs = userBriefMapper.select().colAll().where()
                .gtEq(ShardingUser::getBirthday,  DateUtils.addDays(new Date(), -31))
                .ltEq(ShardingUser::getBirthday,  new Date())
                .exs();
        System.out.println(exs.size());
    }

    @Test
    public void testShardingSampleBetween(){
        List<ShardingUser> exs = userBriefMapper.select().colAll().where()
                .between(ShardingUser::getBirthday,  DateUtils.addDays(new Date(), -31), new Date())
                .exs();
        System.out.println(exs.size());
    }
}
