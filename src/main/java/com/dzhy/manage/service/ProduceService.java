package com.dzhy.manage.service;

import com.dzhy.manage.common.ProduceVO;
import com.dzhy.manage.common.Result;
import com.dzhy.manage.constants.Constants;
import com.dzhy.manage.dao.OutputMapper;
import com.dzhy.manage.dao.ProduceMapper;
import com.dzhy.manage.dao.ProductMapper;
import com.dzhy.manage.dao.ProductSukMapper;
import com.dzhy.manage.entity.Produce;
import com.dzhy.manage.entity.Product;
import com.dzhy.manage.entity.ProductSuk;
import com.dzhy.manage.enums.ProduceEnum;
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
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @ClassName ProduceService
 * @Description 进度管理 service
 * @Author alex
 * @Date 2019-05-17
 **/
@Service
@Slf4j
public class ProduceService {
    private final ProduceMapper produceMapper;
    private final OutputMapper outputMapper;
    private final ProductMapper productMapper;
    private final ProductSukMapper productSukMapper;

    @Autowired
    public ProduceService(ProduceMapper produceMapper,
                          OutputMapper outputMapper,
                          ProductMapper productMapper,
                          ProductSukMapper productSukMapper) {
        this.produceMapper = produceMapper;
        this.outputMapper = outputMapper;
        this.productMapper = productMapper;
        this.productSukMapper = productSukMapper;
    }

    @Transactional(rollbackFor = GeneralException.class)
    public Result addProduce(Integer productId, Integer sukId, Integer num, String comment) throws GeneralException {
        if (productId == null || sukId == null || num == null || StringUtils.isBlank(comment)) {
            throw new GeneralException(ResultEnum.ILLEGAL_PARAMETER.getMessage());
        }
        Product product = productMapper.selectByPrimaryKey(productId);
        if (product == null) {
            throw new GeneralException(ResultEnum.NOT_FOUND.getMessage() + "-ID:" + productId);
        }
        ProductSuk productSuk = productSukMapper.selectByPrimaryKey(sukId);
        if (productSuk == null) {
            throw new GeneralException(ResultEnum.NOT_FOUND.getMessage() + "-sukId:" + sukId);
        }
        int dateInt = CommonUtil.getDateToIntNow();
        Produce exit = produceMapper.selectByDateAndProductIdAndSukId(dateInt, productId, sukId);
        if (exit != null) {
            return Result.isError(ResultEnum.IS_EXIST.getMessage());
        }
        //添加进度，一般只设置下单的值，其他阶段均为0
        Produce insert = Produce.builder()
                .date(dateInt)
                .productId(product.getProductId())
                .produceName(product.getProductName() + ":" + productSuk.getSukName())
                .sukId(sukId)
                .sukPrice(productSuk.getPrice())
                .xiaDan(num)
                .build();

        try {
            //todo 添加record记录
            int count = produceMapper.insertSelective(insert);
            log.info("add produce success, count:{}, produceId:{}", count, insert.getProduceId());
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new GeneralException(ResultEnum.ADD_ERROR.getMessage());
        }
        return Result.isSuccess(insert);
    }


    @Transactional(rollbackFor = GeneralException.class)
    public Result importFromExcel(MultipartFile multipartFile) throws GeneralException, IOException {
        if (multipartFile == null) {
            throw new GeneralException(ResultEnum.ILLEGAL_PARAMETER.getMessage());
        }
        int dateNow = CommonUtil.getDateToIntNow();
        List<Produce> origin = produceMapper.selectByConditions(dateNow, null);
        if (CollectionUtils.isNotEmpty(origin)) {
            return Result.isError("请先将今天的数据清空");
        }

        String fileName = multipartFile.getOriginalFilename();
        log.info("produce fileName = {}", fileName);
        //判断文件类型
        //读取文件内容并存储
        assert fileName != null;
        if (!fileName.substring(fileName.lastIndexOf(ExcelUtils.POINT)).equals(ExcelUtils.EXCEL_2003L)
                && !fileName.substring(fileName.lastIndexOf(ExcelUtils.POINT)).equals(ExcelUtils.EXCEL_2007U)) {
            return Result.isError(ResultEnum.ILLEGAL_FILE_TYPE.getMessage());
        }
        //excel文件读取，写入数据库
        List<Map<String, String>> readResult = ExcelUtils.readToMapList(multipartFile.getInputStream());
        List<Produce> produceList = this.getImportDataFromFile(readResult);
        try {
            int count = produceMapper.insertBatch(produceList);
            log.info("import from database, count:{} listSize:{}", count, produceList.size());
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new GeneralException(ResultEnum.IMPORT_ERROR.getMessage());
        }
        return Result.isSuccess();
    }


    @Transactional(rollbackFor = GeneralException.class)
    public Result importFromDB(int year, int month, int date) throws GeneralException {
        int dateFrom = CommonUtil.getDateToIntOf(year, month, date);
        int dateNow = CommonUtil.getDateToIntNow();
        List<Produce> origin = produceMapper.selectByConditions(dateNow, null);
        if (CollectionUtils.isNotEmpty(origin)) {
            return Result.isError("请先将今天的数据清空");
        }
        List<Produce> produceList = produceMapper.selectByConditions(dateFrom, null);
        if (CollectionUtils.isEmpty(produceList)) {
            return Result.isError("选定的日期没有数据");
        }
        List<Produce> insertList = produceList.stream()
                .map(produce -> Produce.builder()
                        .date(dateNow)
                        .productId(produce.getProductId())
                        .produceName(produce.getProduceName())
                        .sukId(produce.getSukId())
                        .sukPrice(produce.getSukPrice())
                        .xiaDan(produce.getXiaDan())
                        .muGong(produce.getMuGong())
                        .youFang(produce.getYouFang())
                        .baoZhuang(produce.getBaoZhuang())
                        .teDing(produce.getTeDing())
                        .beijing(produce.getBeijing())
                        .beijingTeding(produce.getBeijingTeding())
                        .bendiHetong(produce.getBendiHetong())
                        .waidiHetong(produce.getWaidiHetong())
                .build())
                .collect(Collectors.toList());
        try {
            int count = produceMapper.insertBatch(insertList);
            log.info("import from database, count:{} listSize:{}", count, insertList.size());
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new GeneralException(ResultEnum.IMPORT_ERROR.getMessage());
        }
        return Result.isSuccess();
    }


    public Result exportExcel(int year, int month, int date, OutputStream outputStream) throws GeneralException {
        int dateInt = CommonUtil.getDateToIntOf(year, month, date);
        List<Produce> produceList = produceMapper.selectByConditions(dateInt, null);
        if (CollectionUtils.isEmpty(produceList)) {
            return Result.isError("选定的日期没有数据");
        }
        //计算属性值合计
        ProduceVO totalVo = getTotal(produceList);
        List<ProduceVO> produceVOS = this.transToVo(produceList);
        produceVOS.add(totalVo);
        List<List<String>> list = this.getExportData(produceVOS);
        List<String> headers = this.getExportHeaders();
        String title = dateInt + "\t" + Constants.PRODUCE_TITLE;
        try {
            ExcelUtils.exportData(title, headers, list, outputStream);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new GeneralException(ResultEnum.EXPORT_ERROR.getMessage());
        }
        return Result.isSuccess();
    }

    /*
    @Transactional(rollbackFor = GeneralException.class)
    public Result updateProduce(Produce produce, int flag) throws GeneralException, GeneralException {
        if (produce == null || produce.getProduceId() == null) {
            throw new GeneralException(ResultEnum.ILLEGAL_PARAMETER.getMessage());
        }
        LocalDate date = LocalDate.now();
        //获取 produce source
        Produce produceSource = produceMapper.findByProduceId(produce.getProduceId());
        if (produceSource == null) {
            return Result.isError(ResultEnum.NOT_FOUND.getMessage() + "-ID:" + produce.getProduceId());
        }
        //获取 output source
        Output outputSource = getOutputSource(date.getYear(), date.getMonthValue(),
                produceSource.getProduceProductId(), produceSource.getProduceProductName());

        Produce update = new Produce();
        //判断更新是否可行，正值为生产进度，负值为退单
        if (produce.getProduceXiadan() != null && !updateXiaDan(produce, produceSource, update).isOk()) {
            //下单
            return updateXiaDan(produce, produceSource, update);
        } else if (produce.getProduceMugong() != null
                && !updateMuGong(produce, produceSource, update, outputSource).isOk()) {
            //木工
            return updateMuGong(produce, produceSource, update, outputSource);
        } else if (produce.getProduceYoufang() != null
                && !updateYouFang(produce, produceSource, update, outputSource).isOk()) {
            //油房
            return updateYouFang(produce, produceSource, update, outputSource);
        } else if (produce.getProduceBaozhuang() != null
                && !updateBaoZhuang(produce, produceSource, update, outputSource, flag).isOk()) {
            //包装
            return updateBaoZhuang(produce, produceSource, update, outputSource, flag);
        } else if (produce.getProduceTeding() != null
                && !updateTeDing(produce, produceSource, update, outputSource, flag).isOk()) {
            //特定
            return updateTeDing(produce, produceSource, update, outputSource, flag);
        } else if (produce.getProduceBeijing() != null
                && !updateBeiJing(produce, produceSource, update, outputSource, flag).isOk()) {
            //北京
            return updateBeiJing(produce, produceSource, update, outputSource, flag);
        } else if (produce.getProduceBeijingteding() != null
                && !updateBeiJingTeDing(produce, produceSource, update, outputSource, flag).isOk()) {
            //北京特定
            return updateBeiJingTeDing(produce, produceSource, update, outputSource, flag);
        } else if (produce.getProduceBendihetong() != null
                && !updateBenDiHeTong(produce, produceSource, update).isOk()) {
            //本地合同
            return updateBenDiHeTong(produce, produceSource, update);
        } else if (produce.getProduceWaidihetong() != null
                && !updateWaiDiHeTong(produce, produceSource, update).isOk()) {
            //外地合同
            return updateWaiDiHeTong(produce, produceSource, update);
        } else {
            //return Result.isError(ResultEnum.ILLEGAL_PARAMETER.getMessage() + "数据为空");
            log.error("字段错误");
        }
        try {
            //本地合同、外地合同、等待不影响产值
            if (produce.getProduceBendihetong() == null && produce.getProduceWaidihetong() == null) {
                outputMapper.save(outputSource);
                log.info("update produce, output = {}", outputSource);
            }
            UpdateUtils.copyNullProperties(produceSource, update);
            produceMapper.save(update);
            log.info("update produce success, produce = {}", update);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new GeneralException(ResultEnum.UPDATE_ERROR.getMessage());
        }
        return Result.isSuccess();
    }

    /*
    @Transactional(rollbackFor = GeneralException.class)
    public Result changeProduce(Produce produce) throws GeneralException, GeneralException {
        if (produce == null || produce.getProduceId() == null) {
            throw new GeneralException(ResultEnum.ILLEGAL_PARAMETER.getMessage());
        }
        //获取 produce source
        Produce produceSource = produceMapper.findByProduceId(produce.getProduceId());
        if (produceSource == null) {
            return Result.isError(ResultEnum.NOT_FOUND.getMessage() + "-ID:" + produce.getProduceId());
        }
        Produce update = new Produce();
        if (produce.getProduceXiadan() != null && produce.getProduceXiadan() >= 0) {
            //修正下单
            update.setProduceXiadan(produce.getProduceXiadan());
            update.setProduceXiadanComment(commentAppend(produceSource.getProduceXiadanComment(), "修改为",
                    produce.getProduceXiadan(), produce.getProduceXiadanComment()));
        } else if (produce.getProduceMugong() != null && produce.getProduceMugong() >= 0) {
            //修正木工
            update.setProduceMugong(produce.getProduceMugong());
            update.setProduceMugongComment(commentAppend(produceSource.getProduceMugongComment(), "修改为",
                    produce.getProduceMugong(), produce.getProduceMugongComment()));
        } else if (produce.getProduceYoufang() != null && produce.getProduceYoufang() >= 0) {
            //修正油房
            update.setProduceYoufang(produce.getProduceYoufang());
            update.setProduceYoufangComment(commentAppend(produceSource.getProduceYoufangComment(), "修改为",
                    produce.getProduceYoufang(), produce.getProduceYoufangComment()));
        } else if (produce.getProduceBaozhuang() != null && produce.getProduceBaozhuang() >= 0) {
            //修正包装
            update.setProduceBaozhuang(produce.getProduceBaozhuang());
            update.setProduceBaozhuangComment(commentAppend(produceSource.getProduceBaozhuangComment(), "修改为",
                    produce.getProduceBaozhuang(), produce.getProduceBaozhuangComment()));
        } else if (produce.getProduceTeding() != null && produce.getProduceTeding() >= 0) {
            //修正特定
            update.setProduceTeding(produce.getProduceTeding());
            update.setProduceTedingComment(commentAppend(produceSource.getProduceTedingComment(), "修改为",
                    produce.getProduceTeding(), produce.getProduceTedingComment()));
        } else if (produce.getProduceBeijing() != null && produce.getProduceBeijing() >= 0) {
            //修正北京
            update.setProduceBeijing(produce.getProduceBeijing());
            update.setProduceBeijingComment(commentAppend(produceSource.getProduceBeijingComment(), "修改为",
                    produce.getProduceBeijing(), produce.getProduceBeijingComment()));
        } else if (produce.getProduceBeijingteding() != null && produce.getProduceBeijingteding() >= 0) {
            //修正北京特定
            update.setProduceBeijingteding(produce.getProduceBeijingteding());
            update.setProduceBeijingtedingComment(commentAppend(produceSource.getProduceBeijingtedingComment(), "修改为",
                    produce.getProduceBeijingteding(), produce.getProduceBeijingtedingComment()));
        } else if (produce.getProduceBendihetong() != null && produce.getProduceBendihetong() >= 0) {
            //修正本地合同
            update.setProduceBendihetong(produce.getProduceBendihetong());
            update.setProduceBendihetongComment(commentAppend(produceSource.getProduceBendihetongComment(), "修改为",
                    produce.getProduceBendihetong(), produce.getProduceBendihetongComment()));
        } else if (produce.getProduceWaidihetong() != null && produce.getProduceWaidihetong() >= 0) {
            //修正外地合同
            update.setProduceWaidihetong(produce.getProduceWaidihetong());
            update.setProduceWaidihetongComment(commentAppend(produceSource.getProduceWaidihetongComment(), "修改为",
                    produce.getProduceWaidihetong(), produce.getProduceWaidihetongComment()));
        } else {
            return Result.isError("参数值错误");
        }
        //更新到数据库
        try {
            UpdateUtils.copyNullProperties(produceSource, update);
            log.info("update produce success, produce = {}", update);
            produceMapper.save(update);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new GeneralException(ResultEnum.UPDATE_ERROR.getMessage());
        }
        return Result.isSuccess();
    }
    */

    @Transactional(rollbackFor = GeneralException.class)
    public Result deleteProduceBatch(int year, int month, int date, List<Integer> produceIds) throws GeneralException {
        if (CollectionUtils.isEmpty(produceIds)) {
            throw new GeneralException(ResultEnum.ILLEGAL_PARAMETER.getMessage() + "-produceIds: is empty");
        }
        int dateInt = CommonUtil.getDateToIntOf(year, month, date);
        int dateNow = CommonUtil.getDateToIntNow();
        if (dateNow > dateInt) {
            return Result.isError("不能删除过去日期的数据");
        }
        try {
            int count = produceMapper.deleteBatch(produceIds);
            log.info("delete produce success, count:{} produceIds = {}", count, produceIds);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new GeneralException(ResultEnum.DELETE_ERROR.getMessage());
        }
        return Result.isSuccess();
    }

    @Transactional(rollbackFor = GeneralException.class)
    public Result deleteByDate(int year, int month, int date) throws GeneralException {
        int dateInt = CommonUtil.getDateToIntOf(year, month, date);
        int dateNow = CommonUtil.getDateToIntNow();
        if (dateNow > dateInt) {
            return Result.isError("不能删除过去日期的数据");
        }
        try {
            int count = produceMapper.deleteByDate(dateInt);
            log.info("delete produce success, count:{} dateInt:{}", count, dateInt);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new GeneralException(ResultEnum.DELETE_ERROR.getMessage());
        }
        return Result.isSuccess();
    }


    public Result listProduce(int pageNum, int pageSize,
                              int year, int month, int date, String produceName) throws GeneralException {
        int dateInt = CommonUtil.getDateToIntOf(year, month, date);
        PageHelper.startPage(pageNum, pageSize);
        List<Produce> produceList = produceMapper.selectByConditions(dateInt, produceName);
        PageInfo<Produce> pageInfo = new PageInfo<>(produceList);
        return Result.isSuccess(pageInfo);
    }

    public Result getProduceTotal(int year, int month, int date) {
        int dateInt = CommonUtil.getDateToIntOf(year, month, date);
        List<Produce> produceList = produceMapper.selectByConditions(dateInt, null);
        ProduceVO totalVo = this.getTotal(produceList);
        return Result.isSuccess(totalVo);
    }

    private ProduceVO getTotal(List<Produce> produceList) {
        ProduceVO produceVO;
        if (produceList.size() == 1) {
            produceVO = this.transToVo(produceList.get(0));
        } else {
            produceVO = this.transToVo(produceList).stream()
                    .reduce((x, y) -> ProduceVO.builder()
                            .xiaDan(x.getXiaDan() + y.getXiaDan())
                            .muGong(x.getMuGong() + y.getMuGong())
                            .muGongWorth(x.getMuGongWorth() + y.getMuGongWorth())
                            .youFang(x.getYouFang() + y.getYouFang())
                            .youFangWorth(x.getYouFangWorth() + y.getYouFangWorth())
                            .baoZhuang(x.getBaoZhuang() + y.getBaoZhuang())
                            .baoZhuangWorth(x.getBaoZhuangWorth() + y.getBaoZhuangWorth())
                            .teDing(x.getTeDing() + y.getTeDing())
                            .teDingWorth(x.getTeDingWorth() + y.getTeDingWorth())
                            .beijing(x.getBeijing() + y.getBeijing())
                            .beijingWorth(x.getBeijingWorth() + y.getBeijingWorth())
                            .beijingTeding(x.getBeijingTeding() + y.getBeijingTeding())
                            .beijingTedingWorth(x.getBeijingTedingWorth() + y.getBeijingTedingWorth())
                            .bendiHetong(x.getBendiHetong() + y.getBendiHetong())
                            .bendiHetongWorth(x.getBendiHetongWorth() + y.getBendiHetongWorth())
                            .waidiHetong(x.getWaidiHetong() + y.getWaidiHetong())
                            .waidiHetongWorth(x.getWaidiHetongWorth() + y.getWaidiHetongWorth())
                            .build()
                    )
                    .orElse(ProduceVO.builder()
                            .xiaDan(0)
                            .muGong(0)
                            .muGongWorth(0f)
                            .youFang(0)
                            .youFangWorth(0f)
                            .baoZhuang(0)
                            .baoZhuangWorth(0f)
                            .teDing(0)
                            .teDingWorth(0f)
                            .beijing(0)
                            .beijingWorth(0f)
                            .beijingTeding(0)
                            .beijingTedingWorth(0f)
                            .bendiHetong(0)
                            .bendiHetongWorth(0f)
                            .waidiHetong(0)
                            .waidiHetongWorth(0f)
                            .build()
                    );
        }
        produceVO.setProduceId(null);
        produceVO.setDate(null);
        produceVO.setProductId(null);
        produceVO.setProduceName("合计");
        produceVO.setSukId(null);
        produceVO.setSukPrice(0f);
        return produceVO;
    }

    private ProduceVO transToVo(Produce produce) {
        return new ProduceVO(
                produce.getProduceId(),
                produce.getDate(),
                produce.getProductId(),
                produce.getProduceName(),
                produce.getSukId(),
                produce.getSukPrice(),
                produce.getXiaDan(),
                produce.getMuGong(),
                produce.getYouFang(),
                produce.getBaoZhuang(),
                produce.getTeDing(),
                produce.getBeijing(),
                produce.getBeijingTeding(),
                produce.getBendiHetong(),
                produce.getWaidiHetong(),
                produce.getCreateTime(),
                produce.getUpdateTime()
        );
    }

    private List<ProduceVO> transToVo(List<Produce> produceList) {
        List<ProduceVO> produceVOS = new ArrayList<>(produceList.size());
        for (Produce produce : produceList) {
            produceVOS.add(this.transToVo(produce));
        }
        return produceVOS;
    }

    private List<String> getExportHeaders() {
        return Arrays.asList(
                ProduceEnum.PRODUCE_NAME.getName(),
                ProduceEnum.SUK_PRICE.getName(),
                ProduceEnum.XIA_DAN.getName(),
                ProduceEnum.MU_GONG.getName(), ProduceEnum.PRODUCE_PRICE.getName(),
                ProduceEnum.YOU_FANG.getName(), ProduceEnum.PRODUCE_PRICE.getName(),
                ProduceEnum.BAO_ZHUANG.getName(), ProduceEnum.PRODUCE_PRICE.getName(),
                ProduceEnum.TE_DING.getName(), ProduceEnum.PRODUCE_PRICE.getName(),
                ProduceEnum.BEIJING.getName(), ProduceEnum.PRODUCE_PRICE.getName(),
                ProduceEnum.BEIJING_TEDING.getName(), ProduceEnum.PRODUCE_PRICE.getName(),
                ProduceEnum.BENDI_HETONG.getName(), ProduceEnum.PRODUCE_PRICE.getName(),
                ProduceEnum.WAIDI_HETONG.getName(), ProduceEnum.PRODUCE_PRICE.getName()
        );
    }

    private List<List<String>> getExportData(List<ProduceVO> produceVOS) {
        return produceVOS.stream()
                .map(produceVO -> Arrays.asList(
                        produceVO.getProduceName(),
                        String.valueOf(produceVO.getSukPrice()),
                        String.valueOf(produceVO.getXiaDan()),
                        String.valueOf(produceVO.getMuGong()), String.valueOf(produceVO.getMuGongWorth()),
                        String.valueOf(produceVO.getYouFang()), String.valueOf(produceVO.getYouFangWorth()),
                        String.valueOf(produceVO.getBaoZhuang()), String.valueOf(produceVO.getBaoZhuang()),
                        String.valueOf(produceVO.getTeDing()), String.valueOf(produceVO.getTeDingWorth()),
                        String.valueOf(produceVO.getBeijing()), String.valueOf(produceVO.getBeijingWorth()),
                        String.valueOf(produceVO.getBeijingTeding()), String.valueOf(produceVO.getBeijingTedingWorth()),
                        String.valueOf(produceVO.getBendiHetong()), String.valueOf(produceVO.getBendiHetongWorth()),
                        String.valueOf(produceVO.getWaidiHetong()), String.valueOf(produceVO.getWaidiHetongWorth())
                ))
                .collect(Collectors.toList());
    }

    private List<Produce> getImportDataFromFile(List<Map<String, String>> data) {
        return data.stream()
                .filter(row -> !"合计".equals(row.get(Constants.PRODUCT_NAME)))
                .map(row -> {
                    int count = productMapper.existsByProductName(row.get(ProduceEnum.PRODUCE_NAME.getName()));
                    if (count > 0) {
                        throw new GeneralException(ResultEnum.NOT_FOUND.getMessage() + "-名称:" + row.get(Constants.PRODUCT_NAME));
                    }
                    //todo 导出数据 -> 导入数据
                    return Produce.builder()
                            .build();
                })
                .collect(Collectors.toList());
    }
/*
    private Output getOutputSource(Integer year, Integer month, Integer productId, String productName) {
        boolean isOutputExist = outputMapper.existsByOutputYearAndOutputMonthAndOutputProductId(year, month, productId);
        if (!isOutputExist) {
            Output insert = new Output(null, year, month, productId, productName,
                    0, 0, 0.0F, 0, 0.0F,
                    0, 0.0F, 0, 0.0F,
                    0, 0.0F, 0, 0.0F,
                    0, 0.0F, 0, 0.0F,
                    0, 0.0F, 0, 0.0F,
                    null, null);
            try {
                outputMapper.save(insert);
                log.info("add output success output = {}", insert);
            } catch (Exception e) {
                log.error(e.getMessage());
                throw new GeneralException(ResultEnum.ADD_ERROR.getMessage() + "-产值:" + productName);
            }
        }
        return outputMapper.findByOutputYearAndOutputMonthAndOutputProductId(year, month, productId);
    }
/*
    private Result updateXiaDan(Produce param, Produce produceSource, Produce update) {
        //下单增加
        if (param.getProduceXiadan() == 0) {
            return Result.isError("更新值不能为 0 ");
        }
        if (param.getProduceXiadan() + produceSource.getProduceXiadan() < 0) {
            return Result.isError("更新后，下单值为负数");
        }
        update.setProduceXiadan(param.getProduceXiadan() + produceSource.getProduceXiadan());
        update.setProduceXiadanComment(commentAppend(produceSource.getProduceXiadanComment(), "",
                param.getProduceXiadan(), param.getProduceXiadanComment()));
        return Result.isSuccess();
    }
/*
    private Result updateMuGong(Produce param, Produce produceSource, Produce update, Output outputSource) {
        //进度：木工增加，下单减少
        //产值：下单增加
        if (param.getProduceMugong() == 0) {
            return Result.isError("更新值不能为 0 ");
        } else if (param.getProduceMugong() > produceSource.getProduceXiadan()) {
            return Result.isError("下单库存不足");
        } else if (param.getProduceMugong() + produceSource.getProduceMugong() < 0) {
            return Result.isError("退单超过木工库存");
        } else if (outputSource.getOutputXiadan() + param.getProduceMugong() < 0) {
            return Result.isError("退单后下单产值为负数");
        }
        update.setProduceMugong(param.getProduceMugong() + produceSource.getProduceMugong());
        update.setProduceMugongComment(commentAppend(produceSource.getProduceMugongComment(), "",
                param.getProduceMugong(), param.getProduceMugongComment()));
        produceSource.setProduceXiadan(produceSource.getProduceXiadan() - param.getProduceMugong());
        outputSource.setOutputXiadan(outputSource.getOutputXiadan() + param.getProduceMugong());
        return Result.isSuccess();
    }
/*
    private Result updateYouFang(Produce param, Produce produceSource, Produce update, Output outputSource) {
        //进度：油房增加，木工减少
        //产值：木工增加
        if (param.getProduceYoufang() == 0) {
            return Result.isError("更新值不能为 0 ");
        } else if (param.getProduceYoufang() > produceSource.getProduceMugong()) {
            return Result.isError("木工库存不足");
        } else if (param.getProduceYoufang() + produceSource.getProduceYoufang() < 0) {
            return Result.isError("退单超过油房库存");
        } else if (outputSource.getOutputMugong() + param.getProduceYoufang() < 0) {
            return Result.isError("退单后木工产值为负数");
        }
        //获取产品价格
        Product product = productMapper.findByProductId(produceSource.getProduceProductId());
        if (product == null) {
            return Result.isError(ResultEnum.NOT_FOUND.getMessage() + "-名称:" + produceSource.getProduceProductName());
        }
        update.setProduceYoufang(param.getProduceYoufang() + produceSource.getProduceYoufang());
        update.setProduceYoufangComment(commentAppend(produceSource.getProduceYoufangComment(), "",
                param.getProduceYoufang(), param.getProduceYoufangComment()));
        produceSource.setProduceMugong(produceSource.getProduceMugong() - param.getProduceYoufang());
        //木工产值
        outputSource.setOutputMugong(outputSource.getOutputMugong() + param.getProduceYoufang());
        outputSource.setOutputMugongTotalPrice(outputSource.getOutputMugong() * product.getProductPrice());
        return Result.isSuccess();
    }
/*
    private Result updateBaoZhuang(Produce param, Produce produceSource, Produce update, Output outputSource, int flag) {
        if (param.getProduceBaozhuang() == 0) {
            return Result.isError("更新值不能为 0 ");
        }

        //判断是否工厂出货
        if (flag == Constants.NOT_OUTPUT) {
            //进度：包装增加，油房减少
            //产值：油房增加
            if (param.getProduceBaozhuang() > produceSource.getProduceYoufang()) {
                return Result.isError("油房库存不足");
            } else if (param.getProduceBaozhuang() + produceSource.getProduceBaozhuang() < 0) {
                return Result.isError("退单量超过包装库存");
            } else if (outputSource.getOutputYoufang() + param.getProduceBaozhuang() < 0) {
                return Result.isError("退单后油房产值为负数");
            }
            //获取产品价格
            Product product = productMapper.findByProductId(produceSource.getProduceProductId());
            if (product == null) {
                return Result.isError(ResultEnum.NOT_FOUND.getMessage() + "-名称:" + produceSource.getProduceProductName());
            }
            update.setProduceBaozhuang(param.getProduceBaozhuang() + produceSource.getProduceBaozhuang());
            update.setProduceBaozhuangComment(commentAppend(produceSource.getProduceBaozhuangComment(), "",
                    param.getProduceBaozhuang(), param.getProduceBaozhuangComment()));
            produceSource.setProduceYoufang(produceSource.getProduceYoufang() - param.getProduceBaozhuang());
            //油房产值
            outputSource.setOutputYoufang(outputSource.getOutputYoufang() + param.getProduceBaozhuang());
            outputSource.setOutputYoufangTotalPrice(outputSource.getOutputYoufang() * product.getProductPrice());
        } else {
            //工厂直接出货
            //进度：包装减少
            //产值：工厂出货增加，包装产值增加
            if (param.getProduceBaozhuang() > produceSource.getProduceBaozhuang()) {
                return Result.isError("包装库存不足");
            } else if (outputSource.getOutputBaozhuang() + param.getProduceBaozhuang() < 0) {
                return Result.isError("退单后包装产值为负数");
            } else if (outputSource.getOutputFactoryOutput() + param.getProduceBaozhuang() < 0) {
                return Result.isError("退单后工厂出货产值为负数");
            }
            //获取产品价格
            Product product = productMapper.findByProductId(produceSource.getProduceProductId());
            if (product == null) {
                return Result.isError(ResultEnum.NOT_FOUND.getMessage() + "-名称:" + produceSource.getProduceProductName());
            }
            update.setProduceBaozhuang(produceSource.getProduceBaozhuang() - param.getProduceBaozhuang());
            update.setProduceBaozhuangComment(commentAppend(produceSource.getProduceBaozhuangComment(), "工厂出货",
                    param.getProduceBaozhuang(), param.getProduceBaozhuangComment()));
            //包装产值
            outputSource.setOutputBaozhuang(outputSource.getOutputBaozhuang() + param.getProduceBaozhuang());
            outputSource.setOutputBaozhuangTotalPrice(outputSource.getOutputBaozhuang() * product.getProductPrice());
            //工厂出货
            outputSource.setOutputFactoryOutput(outputSource.getOutputFactoryOutput() + param.getProduceBaozhuang());
            outputSource.setOutputFactoryOutputTotalPrice(outputSource.getOutputFactoryOutput() * product.getProductPrice());
        }
        return Result.isSuccess();
    }
/*
    private Result updateTeDing(Produce param, Produce produceSource, Produce update, Output outputSource, int flag) {
        if (param.getProduceTeding() == 0) {
            return Result.isError("更新值不能为 0 ");
        }
        //判断是否工厂出货
        if (flag == Constants.NOT_OUTPUT) {
            //进度：特定增加，油房减少
            //产值：油房增加
            if (param.getProduceTeding() > produceSource.getProduceYoufang()) {
                return Result.isError("油房库存不足");
            } else if (param.getProduceTeding() + produceSource.getProduceTeding() < 0) {
                return Result.isError("退单量超过特定库存");
            } else if (outputSource.getOutputYoufang() + param.getProduceTeding() < 0) {
                return Result.isError("退单后油房产值为负数");
            }
            //获取产品价格
            Product product = productMapper.findByProductId(produceSource.getProduceProductId());
            if (product == null) {
                return Result.isError(ResultEnum.NOT_FOUND.getMessage() + "-名称:" + produceSource.getProduceProductName());
            }
            update.setProduceTeding(param.getProduceTeding() + produceSource.getProduceTeding());
            update.setProduceTedingComment(commentAppend(produceSource.getProduceTedingComment(), "",
                    param.getProduceTeding(), param.getProduceTedingComment()));
            produceSource.setProduceYoufang(produceSource.getProduceYoufang() - param.getProduceTeding());
            //油房产值
            outputSource.setOutputYoufang(outputSource.getOutputYoufang() + param.getProduceTeding());
            outputSource.setOutputYoufangTotalPrice(outputSource.getOutputYoufang() * product.getProductPrice());
        } else {
            //工厂直接出货
            //进度：特定减少
            //产值：特定工厂出货增加，特定产值增加
            if (param.getProduceTeding() > produceSource.getProduceTeding()) {
                return Result.isError("特定库存不足");
            } else if (outputSource.getOutputTeding() + param.getProduceTeding() < 0) {
                return Result.isError("退单后特定产值为负数");
            } else if (outputSource.getOutputTedingFactoryOutput() + param.getProduceTeding() < 0) {
                return Result.isError("退单后特定工厂出货产值为负数");
            }
            //获取产品价格
            Product product = productMapper.findByProductId(produceSource.getProduceProductId());
            if (product == null) {
                return Result.isError(ResultEnum.NOT_FOUND.getMessage() + "-名称:" + produceSource.getProduceProductName());
            }
            update.setProduceTeding(produceSource.getProduceTeding() - param.getProduceTeding());
            update.setProduceTedingComment(commentAppend(produceSource.getProduceTedingComment(), "工厂直接出货",
                    param.getProduceTeding(), param.getProduceTedingComment()));
            //特定产值
            outputSource.setOutputTeding(outputSource.getOutputTeding() + param.getProduceTeding());
            outputSource.setOutputTedingTotalPrice(outputSource.getOutputTeding() * product.getProductPrice());
            //特定工厂出货
            outputSource.setOutputTedingFactoryOutput(outputSource.getOutputTedingFactoryOutput() + param.getProduceTeding());
            outputSource.setOutputTedingFactoryOutputTotalPrice(outputSource.getOutputTedingFactoryOutput() * product.getProductPrice());
        }

        return Result.isSuccess();
    }
/*
    private Result updateBeiJing(Produce param, Produce produceSource, Produce update, Output outputSource, int flag) {
        if (param.getProduceBeijing() == 0) {
            return Result.isError("更新值不能为 0 ");
        }

        // flag 判断更新是否为北京出货
        if (flag == Constants.NOT_OUTPUT) {
            //进度：北京增加，包装减少
            //产值：包装增加
            if (param.getProduceBeijing() > produceSource.getProduceBaozhuang()) {
                return Result.isError("包装库存不足");
            } else if (param.getProduceBeijing() + produceSource.getProduceBeijing() < 0) {
                return Result.isError("退单量超过包装库存");
            } else if (outputSource.getOutputBaozhuang() + param.getProduceBeijing() < 0) {
                return Result.isError("退单后包装产值为负数");
            } else if (outputSource.getOutputBeijingInput() + param.getProduceBeijing() < 0) {
                return Result.isError("退单后北京入库为负数");
            } else if (outputSource.getOutputBeijingStock() + param.getProduceBeijing() < 0) {
                return Result.isError("退单后北京剩余为负数");
            }
            Product product = productMapper.findByProductId(produceSource.getProduceProductId());
            if (product == null) {
                return Result.isError(ResultEnum.NOT_FOUND.getMessage() + "-名称:" + produceSource.getProduceProductName());
            }
            update.setProduceBeijing(param.getProduceBeijing() + produceSource.getProduceBeijing());
            produceSource.setProduceBaozhuang(produceSource.getProduceBaozhuang() - param.getProduceBeijing());
            update.setProduceBeijingComment(commentAppend(produceSource.getProduceBeijingComment(), "",
                    produceSource.getProduceBeijing(), param.getProduceBeijingComment()));
            //包装产值增加
            outputSource.setOutputBaozhuang(outputSource.getOutputBaozhuang() + param.getProduceBeijing());
            outputSource.setOutputBaozhuangTotalPrice(outputSource.getOutputBaozhuang() * product.getProductPrice());
            //北京入库
            outputSource.setOutputBeijingInput(outputSource.getOutputBeijingInput() + param.getProduceBeijing());
            outputSource.setOutputBeijingInputTotalPrice(outputSource.getOutputBeijingInput() * product.getProductPrice());
            //北京剩余增加
            outputSource.setOutputBeijingStock(outputSource.getOutputBeijingStock() + param.getProduceBeijing());
            outputSource.setOutputBeijingStockTotalPrice(outputSource.getOutputBeijingStock() * product.getProductPrice());
        } else {
            //出货
            //进度：北京减少
            //产值：北京剩余减少
            if (param.getProduceBeijing() > produceSource.getProduceBeijing()) {
                return Result.isError("北京库存不足");
            }
            Product product = productMapper.findByProductId(produceSource.getProduceProductId());
            if (product == null) {
                return Result.isError(ResultEnum.NOT_FOUND.getMessage() + "-名称:" + produceSource.getProduceProductName());
            }
            update.setProduceBeijing(produceSource.getProduceBeijing() - param.getProduceBeijing());
            update.setProduceBeijingComment(commentAppend(produceSource.getProduceBeijingComment(), "出货",
                    produceSource.getProduceBeijing(), param.getProduceBeijingComment()));
            //北京剩余，减少
            outputSource.setOutputBeijingStock(outputSource.getOutputBeijingStock() - param.getProduceBeijing());
            outputSource.setOutputBeijingStockTotalPrice(outputSource.getOutputBeijingStock() * product.getProductPrice());
        }

        return Result.isSuccess();
    }
/*
    private Result updateBeiJingTeDing(Produce param, Produce produceSource, Produce update, Output outputSource, int flag) {
        if (param.getProduceBeijingteding() == 0) {
            return Result.isError("更新值不能为 0 ");
        }
        //判断是否为北京特定出货
        if (flag == Constants.NOT_OUTPUT) {
            //进度：北京特定增加，特定减少
            //产值：特定增加
            if (param.getProduceBeijingteding() > produceSource.getProduceTeding()) {
                return Result.isError("特定库存不足");
            } else if (param.getProduceBeijingteding() + produceSource.getProduceBeijingteding() < 0) {
                return Result.isError("退单量超过北京特定库存");
            } else if (outputSource.getOutputBeijingtedingInput() + param.getProduceBeijingteding() < 0) {
                return Result.isError("退单后北京特定入库为负数");
            } else if (outputSource.getOutputBeijingtedingStock() + param.getProduceBeijingteding() < 0) {
                return Result.isError("退单后北京特定剩余为负数");
            }
            Product product = productMapper.findByProductId(produceSource.getProduceProductId());
            if (product == null) {
                return Result.isError(ResultEnum.NOT_FOUND.getMessage() + "-名称:" + produceSource.getProduceProductName());
            }
            update.setProduceBeijingteding(param.getProduceBeijingteding() + produceSource.getProduceBeijingteding());
            produceSource.setProduceTeding(produceSource.getProduceTeding() - param.getProduceBeijingteding());
            update.setProduceBeijingtedingComment(commentAppend(produceSource.getProduceBeijingtedingComment(), "",
                    produceSource.getProduceBeijingteding(), param.getProduceBeijingtedingComment()));
            //特定产值
            outputSource.setOutputTeding(outputSource.getOutputTeding() + param.getProduceBeijingteding());
            outputSource.setOutputTedingTotalPrice(outputSource.getOutputTeding() * product.getProductPrice());
            //北京特定入库
            outputSource.setOutputBeijingtedingInput(outputSource.getOutputBeijingtedingInput() + param.getProduceBeijingteding());
            outputSource.setOutputBeijingtedingInputTotalPrice(outputSource.getOutputBeijingtedingInput() * product.getProductPrice());
            //北京特定剩余增加
            outputSource.setOutputBeijingtedingStock(outputSource.getOutputBeijingtedingStock() + param.getProduceBeijingteding());
            outputSource.setOutputBeijingtedingStockTotalPrice(outputSource.getOutputBeijingtedingStock() * product.getProductPrice());
        } else {
            //出货
            //进度：北京特定减少./pa
            //产值：北京特定剩余减少
            if (param.getProduceBeijingteding() > produceSource.getProduceBeijingteding()) {
                return Result.isError("北京特定库存不足");
            }
            Product product = productMapper.findByProductId(produceSource.getProduceProductId());
            if (product == null) {
                return Result.isError(ResultEnum.NOT_FOUND.getMessage() + "-名称:" + produceSource.getProduceProductName());
            }
            update.setProduceBeijingteding(produceSource.getProduceBeijingteding() - param.getProduceBeijingteding());
            update.setProduceBeijingtedingComment(commentAppend(produceSource.getProduceBeijingtedingComment(), "出货",
                    produceSource.getProduceBeijingteding(), param.getProduceBeijingtedingComment()));
            //北京剩余，减少
            outputSource.setOutputBeijingtedingStock(outputSource.getOutputBeijingtedingStock() - param.getProduceBeijingteding());
            outputSource.setOutputBeijingtedingStockTotalPrice(outputSource.getOutputBeijingtedingStock() * product.getProductPrice());
        }

        return Result.isSuccess();
    }
/*
    private Result updateBenDiHeTong(Produce param, Produce produceSource, Produce update) {
        //进度：本地合同自己增加，减少
        //产值：没有变化
        if (param.getProduceBendihetong() == 0) {
            return Result.isError("更新值不能为 0 ");
        } else if (param.getProduceBendihetong() + produceSource.getProduceBendihetong() < 0) {
            return Result.isError("退单量超过已有本地合同量");
        }
        update.setProduceBendihetong(param.getProduceBendihetong() + produceSource.getProduceBendihetong());
        update.setProduceBendihetongComment(commentAppend(produceSource.getProduceBendihetongComment(), "",
                param.getProduceBendihetong(), param.getProduceBendihetongComment()));
        return Result.isSuccess();
    }
/*
    private Result updateWaiDiHeTong(Produce param, Produce produceSource, Produce update) {
        //进度：外地合同自己增加，减少
        //产值：没有变化
        if (param.getProduceWaidihetong() == 0) {
            return Result.isError("更新值不能为 0 ");
        } else if (param.getProduceWaidihetong() + produceSource.getProduceWaidihetong() < 0) {
            return Result.isError("退单量超过已有外地合同量");
        }
        update.setProduceWaidihetong(param.getProduceWaidihetong() + produceSource.getProduceWaidihetong());
        update.setProduceWaidihetongComment(commentAppend(produceSource.getProduceWaidihetongComment(), "",
                param.getProduceWaidihetong(), param.getProduceWaidihetongComment()));
        return Result.isSuccess();
    }*/
}
