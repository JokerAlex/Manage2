package com.dzhy.manage.service;

import com.dzhy.manage.common.Result;
import com.dzhy.manage.dao.OutputMapper;
import com.dzhy.manage.dao.OutputRecordMapper;
import com.dzhy.manage.dao.ProductMapper;
import com.dzhy.manage.entity.Output;
import com.dzhy.manage.entity.OutputRecord;
import com.dzhy.manage.entity.Product;
import com.dzhy.manage.enums.OutputEnum;
import com.dzhy.manage.enums.ResultEnum;
import com.dzhy.manage.exception.GeneralException;
import com.dzhy.manage.utils.CommonUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @ClassName OutputService
 * @Description 产值管理 service
 * @Author alex
 * @Date 2019-05-17
 **/
@Service
@Slf4j
public class OutputService {
    private final OutputMapper outputMapper;
    private final ProductMapper productMapper;
    private final OutputRecordMapper outputRecordMapper;

    @Autowired
    public OutputService(OutputMapper outputMapper,
                         ProductMapper productMapper,
                         OutputRecordMapper outputRecordMapper) {
        this.outputMapper = outputMapper;
        this.productMapper = productMapper;
        this.outputRecordMapper = outputRecordMapper;
    }


    public Result listOutput(int pageNum, int pageSize, int year, int month,
                             String productName) throws GeneralException {
        int monthInt = CommonUtil.getMonthToIntOf(year, month);
        PageHelper.startPage(pageNum, pageSize);
        List<Output> outputs = outputMapper.selectByConditions(monthInt, productName);
        PageInfo<Output> pageInfo = new PageInfo<>(outputs);
        return Result.isSuccess(pageInfo);
    }

    @Transactional(rollbackFor = GeneralException.class)
    public Result changeOutput(Long outputId, String key, int value, String comment) throws GeneralException {
        if (outputId == null || StringUtils.isBlank(key)) {
            throw new GeneralException(ResultEnum.ILLEGAL_PARAMETER.getMessage());
        }
        //获取 output source
        Output outputSource = outputMapper.selectByPrimaryKey(outputId);
        if (outputSource == null) {
            return Result.isError(ResultEnum.NOT_FOUND.getMessage() + "-ID:" + outputId);
        }
        Product product = productMapper.selectByPrimaryKey(outputSource.getProductId());
        if (product == null) {
            return Result.isError(ResultEnum.NOT_FOUND.getMessage() + "-产品:" + outputSource.getOutputName());
        }
        OutputEnum outputEnum = OutputEnum.getOutputEnumByCode(key);
        if (outputEnum == null) {
            return Result.isError(ResultEnum.ILLEGAL_PARAMETER.getMessage() + key);
        }
        Output record = this.getOutput(outputSource, outputEnum, value);
        OutputRecord outputRecord = this.getOutputRecord(outputSource, outputEnum, value, comment);
        try {
            int count = outputMapper.updateByPrimaryKeySelective(record);
            log.info("update output success count:{}, outputId:{}, key:{}, value:{}",
                    count, record.getOutputId(), key, value);
            count = outputRecordMapper.insertSelective(outputRecord);
            log.info("insert outputRecord count:{}, outputRecordId:{}",
                    count, outputRecord.getRecordId());
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new GeneralException(ResultEnum.UPDATE_ERROR.getMessage());
        }
        return Result.isSuccess();
    }

    /*
    public Result exportExcel(Integer year, Integer month, OutputStream outputStream) throws GeneralException, GeneralException {
        if (year == null || month == null) {
            throw new GeneralException(ResultEnum.ILLEGAL_PARAMETER.getMessage());
        }
        List<Output> outputList = outputRepository.findAllByOutputYearAndAndOutputMonth(year, month);
        if (CollectionUtils.isEmpty(outputList)) {
            return Result.isError("选定的月份没有数据");
        }
        Output total = getTotal(outputList);
        outputList.add(total);
        List<List<String>> list = outputList.stream()
                .map(output -> {
                    return Arrays.asList(
                            output.getOutputProductName(),
                            String.valueOf(output.getOutputXiadan()),
                            String.valueOf(output.getOutputMugong()),
                            String.valueOf(output.getOutputMugongTotalPrice()),
                            String.valueOf(output.getOutputYoufang()),
                            String.valueOf(output.getOutputYoufangTotalPrice()),
                            String.valueOf(output.getOutputBaozhuang()),
                            String.valueOf(output.getOutputBaozhuangTotalPrice()),
                            String.valueOf(output.getOutputTeding()),
                            String.valueOf(output.getOutputTedingTotalPrice()),
                            String.valueOf(output.getOutputBeijingInput()),
                            String.valueOf(output.getOutputBeijingInputTotalPrice()),
                            String.valueOf(output.getOutputBeijingtedingInput()),
                            String.valueOf(output.getOutputBeijingtedingInputTotalPrice()),
                            String.valueOf(output.getOutputFactoryOutput()),
                            String.valueOf(output.getOutputFactoryOutputTotalPrice()),
                            String.valueOf(output.getOutputTedingFactoryOutput()),
                            String.valueOf(output.getOutputTedingFactoryOutputTotalPrice()),
                            String.valueOf(output.getOutputBeijingStock()),
                            String.valueOf(output.getOutputBeijingStockTotalPrice()),
                            String.valueOf(output.getOutputBeijingtedingStock()),
                            String.valueOf(output.getOutputBeijingtedingStockTotalPrice())
                    );
                })
                .collect(Collectors.toList());
        String title = year + "-" + month + "\t" + Constants.OUTPUT_TITLE;
        List<String> headers = Arrays.asList(Constants.PRODUCT_NAME, Constants.XIA_DAN, Constants.MU_GONG, Constants.MU_GONG_TOTAL_PRICE,
                Constants.YOU_FANG, Constants.YOU_FANG_TOTAL_PRICE, Constants.BAO_ZHUANG, Constants.BAOZHUNAG_TOTAL_PRICE, Constants.TE_DING,
                Constants.TEDING_TOTAL_PRICE, Constants.BEI_JING_INPUT, Constants.BEI_JING_INPUT_TOTAL_PRICE, Constants.BEI_JING_TEDING_INPUT,
                Constants.BEI_JING_TEDING_INPUT_TOTAL_PRICE, Constants.FACTORY_OUTPUT, Constants.FACTORY_OUTPUT_TOTAL_PRICE,
                Constants.TEDING_FACTORY_OUTPUT, Constants.TEDING_FACTORY_OUTPUT_TOTAL_PRICE, Constants.BEIJING_STOCK,
                Constants.BEIJING_STOCK_TOTAL_PRICE, Constants.BEIJINGTEDING_STOCK, Constants.BEIJINGTEDING_STOCK_TOTAL_PRICE);
        try {
            ExcelUtils.exportData(title, headers, list, outputStream);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new GeneralException(ResultEnum.EXPORT_ERROR.getMessage());
        }
        return Result.isSuccess();
    }

    /*
    public Result getOutputTotal(Integer year, Integer month) {
        if (year == null || month == null) {
            throw new GeneralException(ResultEnum.ILLEGAL_PARAMETER.getMessage());
        }
        List<Output> outputList = outputRepository.findAllByOutputYearAndAndOutputMonth(year, month);
        Output total = getTotal(outputList);
        return Result.isSuccess(total);
    }

    private Output getTotal(List<Output> outputList) {
        if (outputList.size() == 1) {
            Output output = new Output();
            UpdateUtils.copyNullProperties(outputList.get(0), output);
            output.setOutputProductName("合计");
            return output;
        } else {
            return outputList.stream()
                    .reduce((x, y) -> new Output()
                            .setOutputXiadan(x.getOutputXiadan() + y.getOutputXiadan())
                            .setOutputMugong(x.getOutputMugong() + y.getOutputMugong())
                            .setOutputMugongTotalPrice(x.getOutputMugongTotalPrice() + y.getOutputMugongTotalPrice())
                            .setOutputYoufang(x.getOutputYoufang() + y.getOutputYoufang())
                            .setOutputYoufangTotalPrice(x.getOutputYoufangTotalPrice() + y.getOutputYoufangTotalPrice())
                            .setOutputBaozhuang(x.getOutputBaozhuang() + y.getOutputBaozhuang())
                            .setOutputBaozhuangTotalPrice(x.getOutputBaozhuangTotalPrice() + y.getOutputBaozhuangTotalPrice())
                            .setOutputTeding(x.getOutputTeding() + y.getOutputTeding())
                            .setOutputTedingTotalPrice(x.getOutputTedingTotalPrice() + y.getOutputTedingTotalPrice())
                            .setOutputBeijingInput(x.getOutputBeijingInput() + y.getOutputBeijingInput())
                            .setOutputBeijingInputTotalPrice(x.getOutputBeijingInputTotalPrice() + y.getOutputBeijingInputTotalPrice())
                            .setOutputBeijingtedingInput(x.getOutputBeijingtedingInput() + y.getOutputBeijingtedingInput())
                            .setOutputBeijingtedingInputTotalPrice(x.getOutputBeijingtedingInputTotalPrice() + y.getOutputBeijingtedingInputTotalPrice())
                            .setOutputFactoryOutput(x.getOutputFactoryOutput() + y.getOutputFactoryOutput())
                            .setOutputFactoryOutputTotalPrice(x.getOutputFactoryOutputTotalPrice() + y.getOutputFactoryOutputTotalPrice())
                            .setOutputTedingFactoryOutput(x.getOutputTedingFactoryOutput() + y.getOutputTedingFactoryOutput())
                            .setOutputTedingFactoryOutputTotalPrice(x.getOutputTedingFactoryOutputTotalPrice() + y.getOutputTedingFactoryOutputTotalPrice())
                            .setOutputBeijingStock(x.getOutputBeijingStock() + y.getOutputBeijingStock())
                            .setOutputBeijingStockTotalPrice(x.getOutputBeijingStockTotalPrice() + y.getOutputBeijingStockTotalPrice())
                            .setOutputBeijingtedingStock(x.getOutputBeijingtedingStock() + y.getOutputBeijingtedingStock())
                            .setOutputBeijingtedingStockTotalPrice(x.getOutputBeijingtedingStockTotalPrice() + y.getOutputBeijingtedingStockTotalPrice())
                    )
                    .orElse(new Output()
                            .setOutputXiadan(0)
                            .setOutputMugong(0)
                            .setOutputMugongTotalPrice(0.0F)
                            .setOutputYoufang(0)
                            .setOutputYoufangTotalPrice(0.0F)
                            .setOutputBaozhuang(0)
                            .setOutputBaozhuangTotalPrice(0.0F)
                            .setOutputTeding(0)
                            .setOutputTedingTotalPrice(0.0F)
                            .setOutputBeijingInput(0)
                            .setOutputBeijingInputTotalPrice(0.0F)
                            .setOutputBeijingtedingInput(0)
                            .setOutputBeijingtedingInputTotalPrice(0.0F)
                            .setOutputFactoryOutput(0)
                            .setOutputFactoryOutputTotalPrice(0.0F)
                            .setOutputTedingFactoryOutput(0)
                            .setOutputTedingFactoryOutputTotalPrice(0.0F)
                            .setOutputBeijingStock(0)
                            .setOutputBeijingStockTotalPrice(0.0F)
                            .setOutputBeijingtedingStock(0)
                            .setOutputBeijingtedingStockTotalPrice(0.0F))
                    .setOutputProductName("合计");
        }
    }*/

    private Output getOutput(Output outputSource, OutputEnum outputEnum, int value) {
        Output record = Output.builder()
                .outputId(outputSource.getOutputId())
                .build();
        switch (outputEnum) {
            case XIA_DAN:
                record.setXiaDan(value);
                break;
            case MU_GONG:
                record.setMuGong(value);
                break;
            case YOU_FANG:
                record.setYouFang(value);
                break;
            case BAO_ZHUANG:
                record.setBaoZhuang(value);
                break;
            case TE_DING:
                record.setTeDing(value);
                break;
            case BEIJING_INPUT:
                record.setBeijingInput(value);
                break;
            case BEIJING_TEDING_INPUT:
                record.setBeijingTedingInput(value);
                break;
            case FACTORY_OUTPUT:
                record.setFactoryOutput(value);
                break;
            case TEDING_FACTORY_OUTPUT:
                record.setTedingFactoryOutput(value);
                break;
            case BEIJING_STOCK:
                record.setBeijingStock(value);
                break;
            case BEIJING_TEDING_STOCK:
                record.setBeijingTedingStock(value);
                break;
            default:
                break;
        }
        return record;
    }

    private OutputRecord getOutputRecord(Output outputSource, OutputEnum outputEnum, int value, String comment) {
        comment = CommonUtil.getUserNameFromContext() + " : " + comment;
        return OutputRecord.builder()
                .userId(CommonUtil.getUserIdFromContext())
                .productId(outputSource.getProductId())
                .sukId(outputSource.getSukId())
                .colName(outputEnum.getCode())
                .value(value)
                .comments(comment)
                .build();
    }
}
