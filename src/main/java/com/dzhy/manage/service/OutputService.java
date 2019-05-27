package com.dzhy.manage.service;

import com.dzhy.manage.common.OutputVO;
import com.dzhy.manage.common.Result;
import com.dzhy.manage.constants.Constants;
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
import com.dzhy.manage.utils.ExcelUtils;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

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
        List<OutputVO> outputVOS = this.transToVo(outputs);
        PageInfo<OutputVO> pageInfo = new PageInfo<>(outputVOS);
        return Result.isSuccess(pageInfo);
    }

    @Transactional(rollbackFor = GeneralException.class)
    public Result fixOutput(Long outputId, String key, int value, String comment) throws GeneralException {
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

    public Result exportExcel(int year, int month, OutputStream outputStream) throws GeneralException {
        int monthInt = CommonUtil.getMonthToIntOf(year, month);
        List<Output> outputList = outputMapper.selectByConditions(monthInt, null);
        if (CollectionUtils.isEmpty(outputList)) {
            return Result.isError("选定的月份没有数据");
        }
        OutputVO totalVO = this.getTotal(outputList);
        List<OutputVO> outputVOS = this.transToVo(outputList);
        outputVOS.add(totalVO);
        List<List<String>> list = getExportData(outputVOS);
        List<String> headers = getExportHeaders();
        String title = year + "-" + month + "\t" + Constants.OUTPUT_TITLE;
        try {
            ExcelUtils.exportData(title, headers, list, outputStream);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new GeneralException(ResultEnum.EXPORT_ERROR.getMessage());
        }
        return Result.isSuccess();
    }

    public Result getOutputTotal(int year, int month) {
        int monthInt = CommonUtil.getMonthToIntOf(year, month);
        List<Output> outputList = outputMapper.selectByConditions(monthInt, null);
        OutputVO totalVO = this.getTotal(outputList);
        return Result.isSuccess(totalVO);
    }

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

    private OutputVO getTotal(List<Output> outputList) {
        OutputVO outputVO;
        if (outputList.size() == 1) {
            outputVO = this.transToVo(outputList.get(0));
        } else {
            outputVO = this.transToVo(outputList).stream()
                    .reduce((x, y) -> OutputVO.builder()
                            .xiaDan(x.getXiaDan() + y.getXiaDan())
                            .muGong(x.getMuGong() + y.getMuGong())
                            .muGongWorth(x.getMuGongWorth() + y.getMuGongWorth())
                            .youFang(x.getYouFang() + y.getYouFang())
                            .youFangWorth(x.getYouFangWorth() + y.getYouFangWorth())
                            .baoZhuang(x.getBaoZhuang() + y.getBaoZhuang())
                            .baoZhuangWorth(x.getBaoZhuangWorth() + y.getBaoZhuangWorth())
                            .teDing(x.getTeDing() + y.getTeDing())
                            .teDingWorth(x.getTeDingWorth() + y.getTeDingWorth())
                            .beijingInput(x.getBeijingInput() + y.getBeijingInput())
                            .beijingInputWorth(x.getBeijingInputWorth() + y.getBeijingInputWorth())
                            .beijingTedingInput(x.getBeijingTedingInput() + y.getBeijingTedingInput())
                            .beijingTedingInputWorth(x.getBeijingTedingInputWorth() + y.getBeijingTedingInputWorth())
                            .factoryOutput(x.getFactoryOutput() + y.getFactoryOutput())
                            .factoryOutputWorth(x.getFactoryOutputWorth() + y.getFactoryOutputWorth())
                            .tedingFactoryOutput(x.getTedingFactoryOutput() + y.getTedingFactoryOutput())
                            .tedingFactoryOutputWorth(x.getTedingFactoryOutputWorth() + y.getTedingFactoryOutputWorth())
                            .beijingStock(x.getBeijingStock() + y.getBeijingStock())
                            .beijingStockWorth(x.getBeijingStockWorth() + y.getBeijingStockWorth())
                            .beijingTedingStock(x.getBeijingTedingStock() + y.getBeijingTedingStock())
                            .beijingTedingStockWorth(x.getBeijingTedingStockWorth() + y.getBeijingTedingStockWorth())
                            .build()
                    )
                    .orElse(
                            OutputVO.builder()
                                    .xiaDan(0)
                                    .muGong(0)
                                    .muGongWorth(0f)
                                    .youFang(0)
                                    .youFangWorth(0f)
                                    .baoZhuang(0)
                                    .baoZhuangWorth(0f)
                                    .teDing(0)
                                    .teDingWorth(0f)
                                    .beijingInput(0)
                                    .beijingInputWorth(0f)
                                    .beijingTedingInput(0)
                                    .beijingTedingInputWorth(0f)
                                    .factoryOutput(0)
                                    .factoryOutputWorth(0f)
                                    .tedingFactoryOutput(0)
                                    .tedingFactoryOutputWorth(0f)
                                    .beijingStock(0)
                                    .beijingStockWorth(0f)
                                    .beijingTedingStock(0)
                                    .beijingTedingStockWorth(0f)
                                    .build()
                    );
        }
        outputVO.setOutputId(null);
        outputVO.setMonth(null);
        outputVO.setProductId(null);
        outputVO.setOutputName("合计");
        outputVO.setSukId(null);
        outputVO.setSukPrice(0f);
        return outputVO;
    }

    private OutputVO transToVo(Output output) {
        return new OutputVO(
                output.getOutputId(),
                output.getMonth(),
                output.getProductId(),
                output.getOutputName(),
                output.getSukId(),
                output.getSukPrice(),
                output.getXiaDan(),
                output.getMuGong(),
                output.getYouFang(),
                output.getBaoZhuang(),
                output.getTeDing(),
                output.getBeijingInput(),
                output.getBeijingTedingInput(),
                output.getFactoryOutput(),
                output.getTedingFactoryOutput(),
                output.getBeijingStock(),
                output.getBeijingTedingStock(),
                output.getCreateTime(),
                output.getUpdateTime()
        );
    }

    private List<OutputVO> transToVo(List<Output> outputList) {
        List<OutputVO> outputVOS = new ArrayList<>(outputList.size());
        for (Output output : outputList) {
            outputVOS.add(this.transToVo(output));
        }
        return outputVOS;
    }

    private List<String> getExportHeaders() {
        return Arrays.asList(
                OutputEnum.OUTPUT_NAME.getName(),
                OutputEnum.SUK_PRICE.getName(),
                OutputEnum.XIA_DAN.getName(),
                OutputEnum.MU_GONG.getName(), OutputEnum.OUTPUT_PRICE.getName(),
                OutputEnum.YOU_FANG.getName(), OutputEnum.OUTPUT_PRICE.getName(),
                OutputEnum.BAO_ZHUANG.getName(), OutputEnum.OUTPUT_PRICE.getName(),
                OutputEnum.TE_DING.getName(), OutputEnum.OUTPUT_PRICE.getName(),
                OutputEnum.BEIJING_INPUT.getName(), OutputEnum.OUTPUT_PRICE.getName(),
                OutputEnum.BEIJING_TEDING_INPUT.getName(), OutputEnum.OUTPUT_PRICE.getName(),
                OutputEnum.FACTORY_OUTPUT.getName(), OutputEnum.OUTPUT_PRICE.getName(),
                OutputEnum.TEDING_FACTORY_OUTPUT.getName(), OutputEnum.OUTPUT_PRICE.getName(),
                OutputEnum.BEIJING_STOCK.getName(), OutputEnum.OUTPUT_PRICE.getName(),
                OutputEnum.BEIJING_TEDING_STOCK.getName(), OutputEnum.OUTPUT_PRICE.getName());
    }

    private List<List<String>> getExportData(List<OutputVO> outputVOS) {
        return outputVOS.stream()
                .map(outputVO -> Arrays.asList(
                        outputVO.getOutputName(),
                        String.valueOf(outputVO.getSukPrice()),
                        String.valueOf(outputVO.getXiaDan()),
                        String.valueOf(outputVO.getMuGong()), String.valueOf(outputVO.getMuGongWorth()),
                        String.valueOf(outputVO.getYouFang()), String.valueOf(outputVO.getYouFangWorth()),
                        String.valueOf(outputVO.getBaoZhuang()), String.valueOf(outputVO.getBaoZhuangWorth()),
                        String.valueOf(outputVO.getTeDing()), String.valueOf(outputVO.getTeDingWorth()),
                        String.valueOf(outputVO.getBeijingInput()), String.valueOf(outputVO.getBeijingInputWorth()),
                        String.valueOf(outputVO.getBeijingTedingInput()), String.valueOf(outputVO.getBeijingTedingInputWorth()),
                        String.valueOf(outputVO.getFactoryOutput()), String.valueOf(outputVO.getFactoryOutputWorth()),
                        String.valueOf(outputVO.getTedingFactoryOutput()), String.valueOf(outputVO.getTedingFactoryOutputWorth()),
                        String.valueOf(outputVO.getBeijingStock()), String.valueOf(outputVO.getBeijingStockWorth()),
                        String.valueOf(outputVO.getBeijingTedingStock()), String.valueOf(outputVO.getBeijingTedingStockWorth())
                ))
                .collect(Collectors.toList());
    }
}
