package com.dzhy.manage.controller;

import com.dzhy.manage.common.Result;
import com.dzhy.manage.constants.Constants;
import com.dzhy.manage.entity.Produce;
import com.dzhy.manage.enums.ResultEnum;
import com.dzhy.manage.service.ProduceService;
import com.dzhy.manage.utils.CommonUtil;
import com.dzhy.manage.utils.ExcelUtils;
import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

/**
 * @ClassName ProduceController
 * @Description 生产进度 controller
 * @Author alex
 * @Date 2019-05-29
 **/
@RestController
@RequestMapping("/produce")
@Api(value = "生产进度", description = "生产进度管理")
@Slf4j
public class ProduceController {

    private final ProduceService iProduceService;

    @Autowired
    public ProduceController(ProduceService iProduceService) {
        this.iProduceService = iProduceService;
    }

    @ApiOperation(value = "添加", notes = "添加新的生产进度")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "productId", value = "生产进度Id", required = true, dataTypeClass = Integer.class),
            @ApiImplicitParam(name = "sukId", value = "sukId", required = true, dataTypeClass = Integer.class),
            @ApiImplicitParam(name = "num", value = "下单数量", required = true, dataTypeClass = Integer.class),
            @ApiImplicitParam(name = "comment", value = "备注", required = true, dataTypeClass = String.class)
    })
    @PreAuthorize("hasAnyAuthority('SYS_ADMIN', 'ADMIN', 'OPERATOR')")
    @PostMapping()
    public Result addProduce(@RequestParam(value = "productId") Integer productId,
                             @RequestParam(value = "sukId") Integer sukId,
                             @RequestParam(value = "num") Integer num,
                             @RequestParam(value = "comment") String comment) {
        return iProduceService.insertProduce(productId, sukId, num, comment);
    }

    @ApiOperation(value = "导入", notes = "数据源：Excel 文件。以导出的 Excel 文件为模版，导入生产进度")
    @PreAuthorize("hasAnyAuthority('SYS_ADMIN', 'ADMIN', 'OPERATOR')")
    @PostMapping(value = "/import/excel", headers = "content-type=multipart/form-data")
    public Result importFromExcel(@ApiParam(value = "文件", required = true) MultipartFile multipartFile) throws Exception {
        return iProduceService.importFromExcel(multipartFile);
    }

    @ApiOperation(value = "导入", notes = "数据源：数据库。选择要导入的日期，将选择日期的内容，导入为今天的数据")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "year", value = "年", required = true, dataTypeClass = Integer.class),
            @ApiImplicitParam(name = "month", value = "月", required = true, dataTypeClass = Integer.class),
            @ApiImplicitParam(name = "day", value = "日", required = true, dataTypeClass = Integer.class)
    })
    @PreAuthorize("hasAnyAuthority('SYS_ADMIN', 'ADMIN', 'OPERATOR')")
    @PostMapping("/import/db")
    public Result importFromDB(@RequestParam(value = "year", defaultValue = "-1") Integer year,
                               @RequestParam(value = "month", defaultValue = "-1") Integer month,
                               @RequestParam(value = "day", defaultValue = "-1") Integer day) {
        return iProduceService.importFromDB(year, month, day);
    }

    @ApiOperation(value = "导出", notes = "手动导出 Excel 文件")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "year", value = "年", required = true, dataTypeClass = Integer.class),
            @ApiImplicitParam(name = "month", value = "月", required = true, dataTypeClass = Integer.class),
            @ApiImplicitParam(name = "day", value = "日", required = true, dataTypeClass = Integer.class)
    })
    @PreAuthorize("hasAnyAuthority('SYS_ADMIN', 'ADMIN', 'OPERATOR', 'USER')")
    @GetMapping("/export")
    public void exportExcel(@RequestParam(value = "year", defaultValue = "-1") Integer year,
                            @RequestParam(value = "month", defaultValue = "-1") Integer month,
                            @RequestParam(value = "day", defaultValue = "-1") Integer day,
                            HttpServletRequest request,
                            HttpServletResponse response) throws IOException {
        String fileName = CommonUtil.getDateToIntOf(year, month, day) + Constants.PRODUCE_TITLE + ExcelUtils.EXCEL_2007U;
        String encoderFileName = CommonUtil.getEncoderFileName(request, fileName);

        response.setContentType("multipart/form-data;charset=UTF-8");
        response.setHeader("Content-Disposition", "attachment; fileName=" + encoderFileName);

        iProduceService.exportExcel(year, month, day, response.getOutputStream());
    }

    @ApiOperation(value = "更新", notes = "生产进度更新，更新后数据库新值为 原始数据库值 + 输入的值")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "produceId", value = "生产进度Id", required = true, dataTypeClass = Integer.class),
            @ApiImplicitParam(name = "key", value = "修改字段", required = true, dataTypeClass = String.class),
            @ApiImplicitParam(name = "value", value = "修改值", required = true, dataTypeClass = Integer.class),
            @ApiImplicitParam(name = "comment", value = "备注", required = true, dataTypeClass = String.class)
    })
    @PreAuthorize("hasAnyAuthority('SYS_ADMIN', 'ADMIN', 'OPERATOR')")
    @PutMapping()
    public Result updateProduce(@RequestParam(value = "produceId") Long produceId,
                                @RequestParam(value = "key") String key,
                                @RequestParam(value = "value") Integer value,
                                @RequestParam(value = "comment") String comment) {
        return iProduceService.updateProduce(false, produceId, key, value, comment, Constants.NOT_OUTPUT);
    }

    @ApiOperation(value = "出货", notes = "包装和特定，北京和北京特定出货")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "produceId", value = "生产进度Id", required = true, dataTypeClass = Integer.class),
            @ApiImplicitParam(name = "key", value = "修改字段", required = true, dataTypeClass = String.class),
            @ApiImplicitParam(name = "value", value = "修改值", required = true, dataTypeClass = Integer.class),
            @ApiImplicitParam(name = "comment", value = "备注", required = true, dataTypeClass = String.class)
    })
    @PreAuthorize("hasAnyAuthority('SYS_ADMIN', 'ADMIN', 'OPERATOR')")
    @PutMapping("/output")
    public Result isOutput(@RequestParam(value = "produceId") Long produceId,
                           @RequestParam(value = "key") String key,
                           @RequestParam(value = "value") Integer value,
                           @RequestParam(value = "comment") String comment) {
        return iProduceService.updateProduce(false, produceId, key, value, comment, Constants.IS_OUTPUT);
    }

    @ApiOperation(value = "错误修正", notes = "错误修正，修正后数据库里的值改变为输入值")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "produceId", value = "生产进度Id", required = true, dataTypeClass = Integer.class),
            @ApiImplicitParam(name = "key", value = "修改字段", required = true, dataTypeClass = String.class),
            @ApiImplicitParam(name = "value", value = "修改值", required = true, dataTypeClass = Integer.class),
            @ApiImplicitParam(name = "comment", value = "备注", required = true, dataTypeClass = String.class)
    })
    @PreAuthorize("hasAnyAuthority('SYS_ADMIN', 'ADMIN', 'OPERATOR')")
    @PutMapping("/change")
    public Result changeProduce(@RequestParam(value = "produceId") Long produceId,
                                @RequestParam(value = "key") String key,
                                @RequestParam(value = "value") Integer value,
                                @RequestParam(value = "comment") String comment) {
        return iProduceService.updateProduce(true, produceId, key, value, comment, -1);
    }

    @ApiOperation(value = "删除", notes = "单个删除、批量删除")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "year", value = "年", required = true, dataTypeClass = Integer.class),
            @ApiImplicitParam(name = "month", value = "月", required = true, dataTypeClass = Integer.class),
            @ApiImplicitParam(name = "day", value = "日", required = true, dataTypeClass = Integer.class)
    })
    @PreAuthorize("hasAnyAuthority('SYS_ADMIN', 'ADMIN', 'OPERATOR')")
    @DeleteMapping()
    public Result deleteProduceBatch(@RequestParam(value = "year", defaultValue = "-1") Integer year,
                                     @RequestParam(value = "month", defaultValue = "-1") Integer month,
                                     @RequestParam(value = "date", defaultValue = "-1") Integer date,
                                     @RequestParam("produceIds[]") List<Integer> produceIds) {
        return iProduceService.deleteProduceBatch(year, month, date, produceIds);
    }

    @ApiOperation(value = "删除", notes = "清空选定日期的数据")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "year", value = "年", required = true, dataTypeClass = Integer.class),
            @ApiImplicitParam(name = "month", value = "月", required = true, dataTypeClass = Integer.class),
            @ApiImplicitParam(name = "day", value = "日", required = true, dataTypeClass = Integer.class)
    })
    @PreAuthorize("hasAnyAuthority('SYS_ADMIN', 'ADMIN', 'OPERATOR')")
    @DeleteMapping("/all")
    public Result deleteAllByDate(@RequestParam(value = "year", defaultValue = "-1") Integer year,
                                  @RequestParam(value = "month", defaultValue = "-1") Integer month,
                                  @RequestParam(value = "date", defaultValue = "-1") Integer date) {
        return iProduceService.deleteByDate(year, month, date);
    }

    @ApiOperation(value = "列表", notes = "获取生产进度列表，分页")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageNum", value = "页码", required = true, dataTypeClass = Integer.class),
            @ApiImplicitParam(name = "pageSize", value = "页面大小", required = true, dataTypeClass = Integer.class),
            @ApiImplicitParam(name = "year", value = "年", required = true, dataTypeClass = Integer.class),
            @ApiImplicitParam(name = "month", value = "月", required = true, dataTypeClass = Integer.class),
            @ApiImplicitParam(name = "day", value = "日", required = true, dataTypeClass = Integer.class),
            @ApiImplicitParam(name = "productName", value = "产品名称，模糊查询使用", dataTypeClass = String.class)
    })
    @PreAuthorize("hasAnyAuthority('SYS_ADMIN', 'ADMIN', 'OPERATOR', 'USER')")
    @GetMapping()
    public Result listProduce(@RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
                              @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize,
                              @RequestParam(value = "year", defaultValue = "-1") Integer year,
                              @RequestParam(value = "month", defaultValue = "-1") Integer month,
                              @RequestParam(value = "date", defaultValue = "-1") Integer date,
                              @RequestParam(value = "productName") String productName) {
        return iProduceService.listProduce(pageNum, pageSize, year, month, date, productName);
    }

    /*@ApiOperation(value = "详情", notes = "获取生产进度详情")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'OPERATOR', 'USER')")
    @GetMapping("/{produceId}")
    public Result getDetails(@PathVariable("produceId") Integer produceId) {
        return iProduceService.getDetails(produceId);
    }*/

    @ApiOperation(value = "合计", notes = "计算各列合计")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "year", value = "年", required = true, dataTypeClass = Integer.class),
            @ApiImplicitParam(name = "month", value = "月", required = true, dataTypeClass = Integer.class),
            @ApiImplicitParam(name = "day", value = "日", required = true, dataTypeClass = Integer.class)
    })
    @PreAuthorize("hasAnyAuthority('SYS_ADMIN', 'ADMIN', 'OPERATOR', 'USER')")
    @GetMapping("/total")
    public Result getTotal(@RequestParam(value = "year") Integer year,
                           @RequestParam(value = "month") Integer month,
                           @RequestParam(value = "day") Integer day) {
        return iProduceService.getProduceTotal(year, month, day);
    }
}

