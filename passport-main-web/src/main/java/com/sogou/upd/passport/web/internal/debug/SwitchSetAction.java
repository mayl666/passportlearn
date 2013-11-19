package com.sogou.upd.passport.web.internal.debug;

import com.sogou.upd.passport.common.CommonConstant;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;

/**
 * Created with IntelliJ IDEA. User: hujunfei Date: 13-6-25 Time: 下午6:39 To change this template use
 * File | Settings | File Templates.
 */
@Controller
@RequestMapping(value = "/internal/switchset")
public class SwitchSetAction {
    @RequestMapping()
    public String indexPage(Model model) throws Exception {
        //
        SwitchObj useIEBBSUniqname = new SwitchObj();
        useIEBBSUniqname.setId(1);
        useIEBBSUniqname.setName("是否使用浏览器论坛昵称");
        int isOpen = CommonConstant.IS_USE_IEBBS_UNIQNAME ? 1 : 0;
        useIEBBSUniqname.setOpen(isOpen);
        model.addAttribute("useIEBBSUniqname", useIEBBSUniqname);

        return "switchset";
    }

    @RequestMapping(value = "/savesetvalue", method = RequestMethod.POST)
    @ResponseBody
    public Object savesetvalue(SwitchObj switchObj) {
        if (switchObj.getId() == 1) {
            CommonConstant.IS_USE_IEBBS_UNIQNAME = (switchObj.getOpen() == 0 ? false : true);
        }
        return "success";
    }
}
