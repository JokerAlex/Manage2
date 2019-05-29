package com.dzhy.manage.controller;

import com.dzhy.manage.common.Result;
import com.dzhy.manage.constants.Constants;
import com.dzhy.manage.service.OutputService;
import com.dzhy.manage.utils.CommonUtil;
import com.dzhy.manage.utils.ExcelUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @ClassName OutputController
 * @Description 产值 controller
 * @Author alex
 * @Date 2019-05-29
 **/
@RestController
@RequestMapping("/output")
@Api(value = "产值", description = "产值管理")
@Slf4j
public class OutputController {
    
    private final OutputService iOutputService;

    @Autowired
    public OutputController(OutputService iOutputService) {
        this.iOutputService = iOutputService;
    }

    @ApiOperation(value = "错误修正", notes = "错误修正，修正后数据库里的值改变为输入值")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "outputId", value = "产值Id", required = true, dataTypeClass = Integer.class),
            @ApiImplicitParam(name = "key", value = "修改字段", required = true, dataTypeClass = String.class),
            @ApiImplicitParam(name = "value", value = "修改值", required = true, dataTypeClass = Integer.class),
            @ApiImplicitParam(name = "comment", value = "备注", required = true, dataTypeClass = String.class)
    })
    @PreAuthorize("hasAnyAuthority('SYS_ADMIN', 'ADMIN', 'OPERATOR')")
    @PutMapping()
    public Result changeOutput(@RequestParam(value = "outputId") Long outputId,
                               @RequestParam(value = "key") String key,
                               @RequestParam(value = "value") Integer value,
                               @RequestParam(value = "comment") String comment) {
        return iOutputService.fixOutput(outputId, key, value, comment);
    }

    @ApiOperation(value = "列表", notes = "获取产值列表，分页")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageNum", value = "页码", required = true, dataTypeClass = Integer.class),
            @ApiImplicitParam(name = "pageSize", value = "页面大小", required = true, dataTypeClass = Integer.class),
            @ApiImplicitParam(name = "year", value = "年", required = true, dataTypeClass = Integer.class),
            @ApiImplicitParam(name = "month", value = "月", required = true, dataTypeClass = Integer.class),
            @ApiImplicitParam(name = "productName", value = "产品名称，模糊查询使用", dataTypeClass = String.class)
    })
    @PreAuthorize("hasAnyAuthority('SYS_ADMIN', 'ADMIN', 'OPERATOR', 'USER')")
    @GetMapping()
    public Result listOutput(@RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
                                  @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize,
                                  @RequestParam(value = "year", defaultValue = "-1") Integer year,
                                  @RequestParam(value = "month", defaultValue = "-1") Integer month,
                                  @RequestParam(value = "productName") String productName) {
        return iOutputService.listOutput(pageNum, pageSize, year, month, productName);
    }

    @ApiOperation(value = "导出", notes = "手动导出 Excel 文件")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "year", value = "年", required = true, dataTypeClass = Integer.class),
            @ApiImplicitParam(name = "month", value = "月", required = true, dataTypeClass = Integer.class)
    })
    @PreAuthorize("hasAnyAuthority('SYS_ADMIN', 'ADMIN', 'OPERATOR', 'USER')")
    @GetMapping("/export")
    public void exportExcel(@RequestParam(value = "year", defaultValue = "-1") Integer year,
                            @RequestParam(value = "month", defaultValue = "-1") Integer month,
                            HttpServletRequest request,
                            HttpServletResponse response) throws Exception {
        String fileName = CommonUtil.getMonthToIntOf(year, month) + Constants.OUTPUT_TITLE + ExcelUtils.EXCEL_2007U;
        String encoderFileName;
        encoderFileName = CommonUtil.getEncoderFileName(request, fileName);

        response.setContentType("multipart/form-data;charset=UTF-8");
        response.setHeader("Content-Disposition", "attachment; fileName=" + encoderFileName);

        iOutputService.exportExcel(year, month, response.getOutputStream());
    }

    @ApiOperation(value = "合计", notes = "计算各列合计")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "year", value = "年", required = true, dataTypeClass = Integer.class),
            @ApiImplicitParam(name = "month", value = "月", required = true, dataTypeClass = Integer.class)
    })
    @PreAuthorize("hasAnyAuthority('SYS_ADMIN', 'ADMIN', 'OPERATOR', 'USER')")
    @GetMapping("/total")
    public Result getTotal(@RequestParam(value = "year") Integer year,
                                @RequestParam(value = "month") Integer month) {
        return iOutputService.getOutputTotal(year, month);
    }
}

