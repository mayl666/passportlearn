package com.sogou.upd.passport.service.impl;

import com.sogou.upd.passport.dao.TestMapper;
import com.sogou.upd.passport.model.UserProfile;
import com.sogou.upd.passport.service.TestService;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: mayan
 * Date: 13-3-12
 * Time: 下午7:02
 * To change this template use File | Settings | File Templates.
 */
@Service
public class TestServiceImpl implements TestService {

    @Inject
    private TestMapper testMapper;

    @Override
    public List<UserProfile> getAllUserProfile() {
        return testMapper.getAllUserProfile();
    }
}
