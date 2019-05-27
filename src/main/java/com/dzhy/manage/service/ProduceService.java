package com.dzhy.manage.service;

import com.dzhy.manage.common.ProduceVO;
import com.dzhy.manage.common.Result;
import com.dzhy.manage.constants.Constants;
import com.dzhy.manage.dao.ProduceMapper;
import com.dzhy.manage.dao.ProductMapper;
import com.dzhy.manage.dao.ProductSukMapper;
import com.dzhy.manage.entity.Produce;
import com.dzhy.manage.entity.Product;
import com.dzhy.manage.entity.ProductSuk;
import com.dzhy.manage.enums.ProduceEnum;
import com.dzhy.manage.enums.ResultEnum;
import com.dzhy.manage.exception.GeneralException;
import com.dzhy.manage.service.produce.*;
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
    private final ProductMapper productMapper;
    private final ProductSukMapper productSukMapper;

    private final BaoZhuangUpdateService baoZhuangUpdateService;
    private final BeijingTedingUpdateService beijingTedingUpdateService;
    private final BeijingUpdateService beijingUpdateService;
    private final BendiHetongUpdateService bendiHetongUpdateService;
    private final MuGongUpdateService muGongUpdateService;
    private final TeDingUpdateService teDingUpdateService;
    private final WaidiHetongUpdateService waidiHetongUpdateService;
    private final XiaDanUpdateService xiaDanUpdateService;
    private final YouFangUpdateService youFangUpdateService;

    @Autowired
    public ProduceService(ProduceMapper produceMapper,
                          ProductMapper productMapper,
                          ProductSukMapper productSukMapper,
                          BaoZhuangUpdateService baoZhuangUpdateService,
                          BeijingTedingUpdateService beijingTedingUpdateService,
                          BeijingUpdateService beijingUpdateService,
                          BendiHetongUpdateService bendiHetongUpdateService,
                          MuGongUpdateService muGongUpdateService,
                          TeDingUpdateService teDingUpdateService,
                          WaidiHetongUpdateService waidiHetongUpdateService,
                          XiaDanUpdateService xiaDanUpdateService,
                          YouFangUpdateService youFangUpdateService) {
        this.produceMapper = produceMapper;
        this.productMapper = productMapper;
        this.productSukMapper = productSukMapper;
        this.baoZhuangUpdateService = baoZhuangUpdateService;
        this.beijingTedingUpdateService = beijingTedingUpdateService;
        this.beijingUpdateService = beijingUpdateService;
        this.bendiHetongUpdateService = bendiHetongUpdateService;
        this.muGongUpdateService = muGongUpdateService;
        this.teDingUpdateService = teDingUpdateService;
        this.waidiHetongUpdateService = waidiHetongUpdateService;
        this.xiaDanUpdateService = xiaDanUpdateService;
        this.youFangUpdateService = youFangUpdateService;
    }

    @Transactional(rollbackFor = GeneralException.class)
    public Result insertProduce(Integer productId, Integer sukId, Integer num, String comment) throws GeneralException {
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
                //produceName -> productName:sukName
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
        return this.getInsertBatchResult(produceList);
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
        return this.getInsertBatchResult(insertList);
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


    @Transactional(rollbackFor = GeneralException.class)
    public Result updateProduce(boolean isFix, Long produceId, String key, int value, String comment,
                                int flag) throws GeneralException {
        if (produceId == null || StringUtils.isBlank(comment)) {
            throw new GeneralException(ResultEnum.ILLEGAL_PARAMETER.getMessage());
        }
        ProduceEnum produceEnum = ProduceEnum.getProduceEnumByCode(key);
        if (produceEnum == null) {
            return Result.isError(ResultEnum.ILLEGAL_PARAMETER.getMessage() + key);
        }
        Produce origin = produceMapper.selectByPrimaryKey(produceId);
        if (origin == null) {
            throw new GeneralException(ResultEnum.NOT_FOUND.getMessage() + "-ID:" + produceId);
        }
        AbstractUpdateService abstractUpdateService = this.getUpdateService(produceEnum);
        if (isFix) {
            return abstractUpdateService.fix(origin, value, comment);
        } else {
            if (CommonUtil.getDateToIntNow() != origin.getDate()) {
                throw new GeneralException(ResultEnum.ILLEGAL_PARAMETER.getMessage() + "-日期" + origin.getDate());
            }
            return abstractUpdateService.update(origin, value, comment, flag);
        }
    }

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

    private Result getInsertBatchResult(List<Produce> insertList) {
        try {
            int count = produceMapper.insertBatch(insertList);
            log.info("import from database, count:{} listSize:{}", count, insertList.size());
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new GeneralException(ResultEnum.IMPORT_ERROR.getMessage());
        }
        return Result.isSuccess();
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
        int dateNow = CommonUtil.getDateToIntNow();
        return data.stream()
                .filter(row -> !"合计".equals(row.get(Constants.PRODUCT_NAME)))
                .map(row -> {
                    String produceName = row.get(ProduceEnum.PRODUCE_NAME.getName());
                    String sukName = produceName.substring(produceName.indexOf(":") + 1);
                    String productName = produceName.substring(0, produceName.indexOf(":"));
                    Product product = productMapper.selectByProductName(productName);
                    if (product == null) {
                        throw new GeneralException(ResultEnum.NOT_FOUND.getMessage() + "-名称:" + productName);
                    }
                    ProductSuk suk = this.getProductSuk(product.getProductId(), sukName);
                    if (suk == null) {
                        throw new GeneralException(ResultEnum.NOT_FOUND.getMessage() + "-Suk名称:" + sukName);
                    }
                    return Produce.builder()
                            .date(dateNow)
                            .productId(product.getProductId())
                            .produceName(product.getProductName() + ":" + suk.getSukName())
                            .sukId(suk.getSukId())
                            .sukPrice(suk.getPrice())
                            .xiaDan(Integer.parseInt(row.get(ProduceEnum.XIA_DAN.getName())))
                            .muGong(Integer.parseInt(row.get(ProduceEnum.MU_GONG.getName())))
                            .youFang(Integer.parseInt(row.get(ProduceEnum.YOU_FANG.getName())))
                            .baoZhuang(Integer.parseInt(row.get(ProduceEnum.BAO_ZHUANG.getName())))
                            .teDing(Integer.parseInt(row.get(ProduceEnum.TE_DING.getName())))
                            .beijing(Integer.parseInt(row.get(ProduceEnum.BEIJING.getName())))
                            .beijingTeding(Integer.parseInt(row.get(ProduceEnum.BEIJING_TEDING.getName())))
                            .bendiHetong(Integer.parseInt(row.get(ProduceEnum.BENDI_HETONG.getName())))
                            .waidiHetong(Integer.parseInt(row.get(ProduceEnum.WAIDI_HETONG.getName())))
                            .build();
                })
                .collect(Collectors.toList());
    }

    private ProductSuk getProductSuk(int productId, String sukName) {
        List<ProductSuk> sukList = productSukMapper.selectByProductId(productId);
        for (ProductSuk suk : sukList) {
            if (sukName.equals(suk.getSukName())) {
                return suk;
            }
        }
        return null;
    }

    private AbstractUpdateService getUpdateService(ProduceEnum produceEnum) {
        AbstractUpdateService abstractUpdateService;
        switch (produceEnum) {
            case XIA_DAN:
                abstractUpdateService = xiaDanUpdateService;
                break;
            case MU_GONG:
                abstractUpdateService = muGongUpdateService;
                break;
            case YOU_FANG:
                abstractUpdateService = youFangUpdateService;
                break;
            case BAO_ZHUANG:
                abstractUpdateService = baoZhuangUpdateService;
                break;
            case TE_DING:
                abstractUpdateService = teDingUpdateService;
                break;
            case BEIJING:
                abstractUpdateService = beijingUpdateService;
                break;
            case BEIJING_TEDING:
                abstractUpdateService = beijingTedingUpdateService;
                break;
            case BENDI_HETONG:
                abstractUpdateService = bendiHetongUpdateService;
                break;
            case WAIDI_HETONG:
                abstractUpdateService = waidiHetongUpdateService;
                break;
            default:
                abstractUpdateService = null;
                break;
        }
        return abstractUpdateService;
    }
}
