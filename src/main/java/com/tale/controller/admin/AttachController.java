package com.tale.controller.admin;

import com.tale.constant.TaleConst;
import com.tale.controller.BaseController;
import com.tale.dto.LogActions;
import com.tale.model.Bo.RestResponseBo;
import com.tale.dto.Types;
import com.tale.model.entity.Attach;
import com.tale.model.entity.Users;
import com.tale.service.AttachService;
import com.tale.service.LogsService;
import com.tale.utils.Commons;
import com.tale.utils.TaleUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by hpxl on 1/4/18.
 */
@Controller
@RequestMapping("admin/attach")
public class AttachController  extends BaseController {
    private static final Logger LOGGER = LoggerFactory.getLogger(AttachController.class);

    public static final String CLASSPATH = TaleUtils.getUplodFilePath();

    @Autowired
    private AttachService attachService;

    @Autowired
    private LogsService logsService;

    /**
     * 附件页面
     * @param request
     * @param page
     * @param limit
     * @return
     */
    @GetMapping(value = "")
    public String index (HttpServletRequest request, @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam( value = "limit", defaultValue = "12") int limit) {
        List<Attach> attaches = attachService.getAttachs(page, limit);
        request.setAttribute("attachs", attaches);
        request.setAttribute(Types.ATTACH_URL, Commons.site_option(Types.ATTACH_URL, Commons.site_url()));
        request.setAttribute("max_file_size", TaleConst.MAX_FILE_SIZE / 1024);
        return "admin/attach";
    }

    /**
     * 上传文件接口
     *
     * @param request
     * @param multipartFiles
     * @return
     */
    @PostMapping(value = "upload")
    @ResponseBody
    public RestResponseBo upload(HttpServletRequest request, @RequestParam("file") MultipartFile[] multipartFiles)
        throws IOException {
        Users users = this.user(request);
        Integer uid = users.getUid();
        List<String> errorFiles = new ArrayList<String>();
        try {
            for (MultipartFile multipartFile : multipartFiles) {
                String fname = multipartFile.getOriginalFilename();
                if (multipartFile.getSize() <= TaleConst.MAX_FILE_SIZE) {
                    String fkey = TaleUtils.getFileKey(fname);
                    String ftype = TaleUtils.isImage(multipartFile.getInputStream()) ? Types.IMAGE : Types.FILE;
                    File file = new File(CLASSPATH + fkey);
                    try {
                        FileCopyUtils.copy(multipartFile.getInputStream(), new FileOutputStream(file));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    attachService.save(fname, ftype, fkey, uid);
                } else {
                    errorFiles.add(fname);
                }
            }
        } catch (Exception e) {
            return RestResponseBo.fail();
        }
        return RestResponseBo.ok(errorFiles);
    }

    @PostMapping(value="delete")
    @ResponseBody
    public RestResponseBo delete(@RequestParam Integer id, HttpServletRequest request) {
        try {
            Attach attach = attachService.selectById(id);
            if (null == attach) {
                return RestResponseBo.fail("不存在该附件");
            }
            attachService.deleteById(attach.getId());
            new File(CLASSPATH + attach.getFkey()).delete();
            logsService.insertLog(LogActions.DEL_ARTICLE.getAction(), attach.getFkey(),
                    request.getRemoteAddr(), this.getUid(request));
        } catch (Exception e) {
            String msg = "附件删除失败";
            LOGGER.error(msg, e);
            return RestResponseBo.fail(msg);
        }
        return RestResponseBo.ok();
    }

}
