package com.nano.activity.devicedata.healthrecord;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.button.MaterialButton;
import android.support.v7.widget.AppCompatCheckBox;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;

import com.alibaba.fastjson.JSON;
import com.nano.activity.devicedata.devicechoose.DeviceChooseActivity;
import com.nano.activity.devicedata.healthrecord.watcher.AdmissionIdWatcher;
import com.nano.activity.devicedata.healthrecord.watcher.HospitalOperationNumberWatcher;
import com.nano.activity.devicedata.healthrecord.watcher.PatientAgeWatcher;
import com.nano.activity.devicedata.healthrecord.watcher.PatientHeightWatcher;
import com.nano.activity.devicedata.healthrecord.watcher.PatientIdWatcher;
import com.nano.activity.devicedata.healthrecord.watcher.PatientWeightWatcher;
import com.nano.common.logger.Logger;
import com.nano.AppStatic;
import com.nano.common.threadpool.ThreadPoolUtils;
import com.nano.common.util.ToastUtil;
import com.nano.R;
import com.nano.common.logger.LoggerFactory;
import com.sdsmdg.tastytoast.TastyToast;

import org.angmarch.views.NiceSpinner;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

/**
 * Description: 电子病历界面
 *
 * @version: 1.0
 * @author: nano
 * @date: 2020/10/23 10:55
 */
public class HealthRecordActivity extends Activity {

    /**
     * 医院省份Spinner控件
     */
    private NiceSpinner spinnerHospitalArea;

    /**
     * 医院等级Spinner控件
     */
    private NiceSpinner spinnerHospitalLevel;

    /**
     * 医院名称TextView控件
     */
    private AutoCompleteTextView atHospitalName;

    /**
     * 身份证号码EditText控件
     */
    private EditText etPatientId;

    /**
     * 住院号EditText控件
     */
    private EditText etAdmissionId;

    /**
     * 病人年龄EditText控件
     */
    private EditText etPatientAge;

    /**
     * 病人身高EditText控件
     */
    private EditText etPatientHeight;

    /**
     * 体重EditText控件
     */
    private EditText etPatientWeight;

    /**
     * 病人性别Ratio Button控件组
     */
    private RadioButton rbPatientSexMan;
    private RadioButton rbPatientSexWoman;

    /**
     * 手术名称EditText控件
     */
    private EditText etOperationName;
    private ImageView ivChooseOperationName;

    /**
     * 医院手麻系统对应的手术序列号
     */
    private EditText etHospitalOperationNumber;

    /**
     * 术前诊断
     */
    private EditText etBeforeOperationDiagnosis;

    /**
     * 是否急诊Ratio Button控件组
     */
    private RadioButton rbIsUrgentYes;
    private RadioButton rbIsUrgentNo;

    /**
     * ASA等级Ratio Button控件组
     */
    private RadioButton rbASALevel1;
    private RadioButton rbASALevel2;
    private RadioButton rbASALevel3;
    private RadioButton rbASALevel4;
    private RadioButton rbASALevel5;

    /**
     * 心功能、肺功能、肝功能、肾功能分级
     */
    private NiceSpinner spinnerHeartFunctionLevel;
    private NiceSpinner spinnerLungFunctionLevel;
    private NiceSpinner spinnerLiverFunctionLevel;
    private NiceSpinner spinnerKidneyFunctionLevel;

    /**
     * 麻醉方式Spinner控件
     */
    private AppCompatCheckBox boxJuMa;
    private AppCompatCheckBox boxYaoMa;
    private AppCompatCheckBox boxMacMaZui;
    private AppCompatCheckBox boxZuZhiMaZui;
    private AppCompatCheckBox boxXiRuMaZui;
    private AppCompatCheckBox boxJingXiFuHeMaZui;
    private AppCompatCheckBox boxQuanPingJingMaiMaZui;
    private AppCompatCheckBox boxYaoYingLianHeMaZui;
    private AppCompatCheckBox boxChiXuYingMoWaiMaZui;

    private List<AppCompatCheckBox> maZuiFangShiCheckBoxes;

    /**
     * 既往病史和特殊疾病情况控件
     */
    private EditText etMedicalHistory;
    private EditText etSpecialDiseaseCase;


    /**
     * 采集基本信息实体（含病人信息和仪器信息）
     */
    private CollectionBasicInfoEntity collectionBasicInfoEntity;


    /**
     * 日志
     */
    private Logger logger = LoggerFactory.getLogger("[HealthRecordActivity]");


    @SuppressLint("UseSparseArrays")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Remove the title bar
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_health_record);

        collectionBasicInfoEntity = AppStatic.collectionBasicInfoEntity;

        // 提交按钮
        /**
         * 提交信息的按钮
         */
        MaterialButton btnCommit = findViewById(R.id.health_record_button_commit);

        // 医院地区
        spinnerHospitalArea = findViewById(R.id.health_record_spinner_hospitalArea);
        List<String> hospitalAreaSet = new LinkedList<>(HealthRecordCons.HOSPITAL_AREA_LIST);
        spinnerHospitalArea.attachDataSource(hospitalAreaSet);
        spinnerHospitalArea.setSelectedIndex(1);

        // 医院名称
        atHospitalName = findViewById(R.id.health_record_hospital_name);
        // 放入医院名称可以实现提示输入
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, R.layout.item_autofill_edittext, HealthRecordCons.HOSPITAL_NAME);
        atHospitalName.setAdapter(arrayAdapter);
        // 设置默认显示的名称
        atHospitalName.setText(HealthRecordCons.XIN_QIAO_HOSPITAL_NAME);

        // 医院等级
        final List<String> hospitalLevels = new LinkedList<>(HealthRecordCons.HOSPITAL_LEVELS);
        spinnerHospitalLevel = findViewById(R.id.health_record_spinner_hospitalLevel);
        spinnerHospitalLevel.attachDataSource(hospitalLevels);
        spinnerHospitalLevel.setSelectedIndex(2);

        etPatientId = findViewById(R.id.health_record_person_ID);
        etAdmissionId = findViewById(R.id.health_record_hospitalLivingID);
        etPatientAge = findViewById(R.id.health_record_age);
        etPatientHeight = findViewById(R.id.health_record_height);
        etPatientWeight = findViewById(R.id.health_record_weight);
        rbPatientSexMan = findViewById(R.id.health_record_ratioButton_man);
        rbPatientSexWoman = findViewById(R.id.health_record_ratioButton_woman);
        etOperationName = findViewById(R.id.health_record_operation_name);
        etHospitalOperationNumber = findViewById(R.id.health_record_hospital_operation_number);
        etBeforeOperationDiagnosis = findViewById(R.id.health_record_before_operation_diagnosis);
        rbIsUrgentYes = findViewById(R.id.health_record_ratioButton_urgent);
        rbIsUrgentNo = findViewById(R.id.health_record_ratioButton_urgentNo);
        rbASALevel1 = findViewById(R.id.health_record_ratioGroup_ASALevel_1);
        rbASALevel2 = findViewById(R.id.health_record_ratioGroup_ASALevel_2);
        rbASALevel3 = findViewById(R.id.health_record_ratioGroup_ASALevel_3);
        rbASALevel4 = findViewById(R.id.health_record_ratioGroup_ASALevel_4);
        rbASALevel5 = findViewById(R.id.health_record_ratioGroup_ASALevel_5);
        spinnerHeartFunctionLevel = findViewById(R.id.health_record_heart_function_level);
        spinnerLungFunctionLevel = findViewById(R.id.health_record_lung_function_level);
        spinnerLiverFunctionLevel = findViewById(R.id.health_record_liver_function_level);
        spinnerKidneyFunctionLevel = findViewById(R.id.health_record_kidney_function_level);
        spinnerHeartFunctionLevel.attachDataSource(HealthRecordCons.HEART_FUNCTION_LEVEL);
        spinnerLungFunctionLevel.attachDataSource(HealthRecordCons.LUNG_FUNCTION_LEVEL);
        spinnerLiverFunctionLevel.attachDataSource(HealthRecordCons.LIVER_FUNCTION_LEVEL);
        spinnerKidneyFunctionLevel.attachDataSource(HealthRecordCons.KIDENY_FUNCTION_LEVEL);

        // 麻醉方式
        boxJuMa = findViewById(R.id.health_record_mazuifangshi_ju_ma);
        boxYaoMa = findViewById(R.id.health_record_mazuifangshi_yao_ma);
        boxMacMaZui = findViewById(R.id.health_record_mazuifangshi_mac_ma_zui);
        boxZuZhiMaZui = findViewById(R.id.health_record_mazuifangshi_zu_zhi_ma_zui);
        boxXiRuMaZui = findViewById(R.id.health_record_mazuifangshi_xi_ru_ma_zui);
        boxJingXiFuHeMaZui = findViewById(R.id.health_record_mazuifangshi_jing_xi_fu_he_ma_zui);
        boxQuanPingJingMaiMaZui = findViewById(R.id.health_record_mazuifangshi_quan_ping_jing_mai_ma_zui);
        boxYaoYingLianHeMaZui = findViewById(R.id.health_record_mazuifangshi_yao_ying_lian_he_ma_zui);
        boxChiXuYingMoWaiMaZui = findViewById(R.id.health_record_mazuifangshi_chi_xu_ying_mo_wai_ma_zui);
        maZuiFangShiCheckBoxes = new ArrayList<>();
        maZuiFangShiCheckBoxes.add(boxJuMa);
        maZuiFangShiCheckBoxes.add(boxYaoMa);
        maZuiFangShiCheckBoxes.add(boxMacMaZui);
        maZuiFangShiCheckBoxes.add(boxZuZhiMaZui);
        maZuiFangShiCheckBoxes.add(boxXiRuMaZui);
        maZuiFangShiCheckBoxes.add(boxJingXiFuHeMaZui);
        maZuiFangShiCheckBoxes.add(boxQuanPingJingMaiMaZui);
        maZuiFangShiCheckBoxes.add(boxYaoYingLianHeMaZui);
        maZuiFangShiCheckBoxes.add(boxChiXuYingMoWaiMaZui);

        // 既往病史
        etMedicalHistory = findViewById(R.id.health_record_operation_sick_history);
        // 特殊病情
        etSpecialDiseaseCase = findViewById(R.id.health_record_operation_special_disease_case);

        // 测试按钮
        ImageView ivTestButton = findViewById(R.id.patient_info_debug_button);
        ivTestButton.setOnClickListener(v -> {
            // 使用默认的病人信息
            setDefaultPatientInfo();
            Intent intent = new Intent(HealthRecordActivity.this, DeviceChooseActivity.class);
            startActivity(intent);
        });

        // 选择手术名称的按钮,开启新的Activity
        ivChooseOperationName = findViewById(R.id.patient_info_choose_operation_name);
        ivChooseOperationName.setOnClickListener(v -> {
            Intent intent = new Intent(HealthRecordActivity.this, OperationNameSearchActivity.class);
            startActivity(intent);
        });

        // 监控输入的内容 用于判断输入的信息格式是否合格
        etPatientId.addTextChangedListener(new PatientIdWatcher(etPatientId, getApplicationContext()));
        etAdmissionId.addTextChangedListener(new AdmissionIdWatcher(etAdmissionId, getApplicationContext()));
        etPatientAge.addTextChangedListener(new PatientAgeWatcher(etPatientAge, getApplicationContext()));
        etPatientHeight.addTextChangedListener(new PatientHeightWatcher(etPatientHeight, getApplicationContext()));
        etPatientWeight.addTextChangedListener(new PatientWeightWatcher(etPatientWeight, getApplicationContext()));
        etHospitalOperationNumber.addTextChangedListener(new HospitalOperationNumberWatcher(etHospitalOperationNumber, getApplicationContext()));

        // 提交病人信息
        btnCommit.setOnClickListener(v -> {

            /* 获取控件值并存入全局变量中 */
            // 医院地区
            String hospitalArea = HealthRecordUtils.getHospitalArea(spinnerHospitalArea.getSelectedIndex());
            collectionBasicInfoEntity.setHospitalArea(hospitalArea);

            // 医院等级
            String hospitalLevel = HealthRecordUtils.getHospitalLevel(spinnerHospitalLevel.getSelectedIndex());
            collectionBasicInfoEntity.setHospitalLevel(hospitalLevel);

            // 医院代号（名称）
            String hospitalCode = atHospitalName.getText().toString().trim();
            collectionBasicInfoEntity.setHospitalCode(hospitalCode);

//            // 病人ID（身份证号码）
//            String patientID = etPatientId.getText().toString().trim();
//            if (patientID.length() != 0) {
//                // 将可能出现的X转换为大写
//                patientID = patientID.toUpperCase();
//            }
//            collectionBasicInfoEntity.setPatientId(patientID);

            // 住院号
//            String admissionId = etAdmissionId.getText().toString().trim();
//            collectionBasicInfoEntity.setAdmissionId(admissionId);

            // 病人年龄
            String patientAge = etPatientAge.getText().toString().trim();
            collectionBasicInfoEntity.setPatientAge(patientAge);

            // 病人身高
            String patientHeight = etPatientHeight.getText().toString().trim();
            collectionBasicInfoEntity.setPatientHeight(patientHeight);

            // 病人体重
            String patientWeight = etPatientWeight.getText().toString().trim();
            collectionBasicInfoEntity.setPatientWeight(patientWeight);

            // 病人性别
            String patientSex = getPatientSex();
            collectionBasicInfoEntity.setPatientSex(patientSex);

            // 手术名称
            String operationName = etOperationName.getText().toString().trim();
            collectionBasicInfoEntity.setOperationName(operationName);

            // 手麻系统对应的手术序列号
            String hospitalOperationNumber = etHospitalOperationNumber.getText().toString().trim();
            collectionBasicInfoEntity.setHospitalOperationNumber(hospitalOperationNumber);

            // 术前诊断
            String beforeOperationDiagnosis = etBeforeOperationDiagnosis.getText().toString().trim();
            // 如果输入是""则默认填写无
            beforeOperationDiagnosis = "".equals(beforeOperationDiagnosis) ? "无" : beforeOperationDiagnosis;
            collectionBasicInfoEntity.setBeforeOperationDiagnosis(beforeOperationDiagnosis);

            // 是否急诊
            Boolean operationIsUrgent = getUrgentInfo();
            collectionBasicInfoEntity.setOperationIsUrgent(operationIsUrgent);

            // ASA等级
            Integer operationASALevel = getASALevel();
            collectionBasicInfoEntity.setOperationASALevel(operationASALevel);

            // 获取心肺肝肾功能等级
            collectionBasicInfoEntity.setOperationHeartFunctionLevel(spinnerHeartFunctionLevel.getSelectedIndex());
            collectionBasicInfoEntity.setOperationLungFunctionLevel(spinnerLungFunctionLevel.getSelectedIndex());
            collectionBasicInfoEntity.setOperationLiverFunctionLevel(spinnerLiverFunctionLevel.getSelectedIndex());
            collectionBasicInfoEntity.setOperationKidneyFunctionLevel(spinnerKidneyFunctionLevel.getSelectedIndex());

            // 麻醉方式
            String operationAnesthesiaMode = getAnesthesiaMode();
            collectionBasicInfoEntity.setOperationAnesthesiaMode(operationAnesthesiaMode);

            // 既往病史 当输入为空时默认为"无"
            String pastMedicalHistory = etMedicalHistory.getText().toString().trim();
            pastMedicalHistory = "".equals(pastMedicalHistory) ? "无" : pastMedicalHistory;
            collectionBasicInfoEntity.setPastMedicalHistory(pastMedicalHistory);

            // 特殊情况 当输入为空时默认为"无"
            String specialDiseaseCase = etSpecialDiseaseCase.getText().toString().trim();
            specialDiseaseCase = "".equals(specialDiseaseCase) ? "无" : specialDiseaseCase;
            collectionBasicInfoEntity.setSpecialDiseaseCase(specialDiseaseCase);

            // 判断各控件的取值是否完成，完成则进入仪器选择Activity
            if (HealthRecordCons.NO_INPUT.equals(hospitalArea)) {
                ToastUtil.toast(getApplicationContext(), "尚未选择医院地区", TastyToast.WARNING);
            } else if (HealthRecordCons.NO_INPUT.equals(hospitalLevel)) {
                ToastUtil.toast(getApplicationContext(), "尚未选择医院等级", TastyToast.WARNING);
            } else if (HealthRecordCons.NO_INPUT.equals(hospitalCode)) {
                ToastUtil.toast(getApplicationContext(), "尚未填写医院名称", TastyToast.WARNING);
            }
//            else if (HealthRecordCons.NO_INPUT.equals(patientID)) {
//                ToastUtil.toast(getApplicationContext(), "尚未填写病人身份证号", TastyToast.WARNING);
//            } else if (HealthRecordCons.NO_INPUT.equals(admissionId)) {
//                ToastUtil.toast(getApplicationContext(), "尚未填写住院号", TastyToast.WARNING);
//            }
            else if (HealthRecordCons.NO_INPUT.equals(patientAge)) {
                ToastUtil.toast(getApplicationContext(), "尚未填写年龄", TastyToast.WARNING);
            } else if (HealthRecordCons.NO_INPUT.equals(patientHeight)) {
                ToastUtil.toast(getApplicationContext(), "尚未填写身高", TastyToast.WARNING);
            } else if (HealthRecordCons.NO_INPUT.equals(patientWeight)) {
                ToastUtil.toast(getApplicationContext(), "尚未填写体重", TastyToast.WARNING);
            } else if (!rbPatientSexMan.isChecked() && !rbPatientSexWoman.isChecked()) {
                ToastUtil.toast(getApplicationContext(), "尚未选择性别", TastyToast.WARNING);
            } else if (HealthRecordCons.NO_INPUT.equals(operationName)) {
                ToastUtil.toast(getApplicationContext(), "尚未填写手术名称", TastyToast.WARNING);
            } else if (HealthRecordCons.NO_INPUT.equals(hospitalOperationNumber)) {
                ToastUtil.toast(getApplicationContext(), "尚未填写手麻系统手术序列号", TastyToast.WARNING);
            } else if (!rbIsUrgentYes.isChecked() && !rbIsUrgentNo.isChecked()) {
                ToastUtil.toast(getApplicationContext(), "尚未选择是否急诊", TastyToast.WARNING);
            } else if (!rbASALevel1.isChecked() && !rbASALevel2.isChecked() && !rbASALevel3.isChecked() && !rbASALevel4.isChecked() && !rbASALevel5.isChecked()) {
                ToastUtil.toast(getApplicationContext(), "尚未选择ASA等级", TastyToast.WARNING);
            } else if (HealthRecordCons.NO_INPUT.equals(operationAnesthesiaMode)) {
                ToastUtil.toast(getApplicationContext(), "尚未选择麻醉方式", TastyToast.WARNING);
            } else {
                // 验证输入之后进入仪器选择Activity
                if (verifyInput()) {

                    EmrEntity emrEntity = new EmrEntity();
                    emrEntity.setPatientId("500281196003027281");
                    emrEntity.setAdmissionId("A237892");
                    emrEntity.setPatientAge("60");
                    emrEntity.setPatientHeight("176cm");
                    emrEntity.setPatientWeight("70kg");
                    emrEntity.setPatientSex("男");
                    emrEntity.setOperationName("胸腔镜下肺修补术");
                    emrEntity.setHospitalOperationNumber("AS123890");
                    emrEntity.setBeforeOperationDiagnosis("咳嗽3年,肺部病变");
                    emrEntity.setOperationIsUrgent("否");
                    emrEntity.setOperationASALevel("3级");
                    emrEntity.setOperationHeartFunctionLevel("II级");
                    emrEntity.setOperationLungFunctionLevel("3级");
                    emrEntity.setOperationLiverFunctionLevel("A级");
                    emrEntity.setOperationKidneyFunctionLevel("3级");
                    emrEntity.setPastMedicalHistory("糖尿病,高血压病史4年,17年进行肾结石手术.");
                    emrEntity.setSpecialDiseaseCase("糖尿病，高血压");

                    // 缓存这个数据
                    AppStatic.emrData = JSON.toJSONString(emrEntity);

                    // 记录日志
                    logger.info("术前EHR信息:" + collectionBasicInfoEntity.toString());
                    Intent intent = new Intent(HealthRecordActivity.this, DeviceChooseActivity.class);
                    startActivity(intent);
                }
            }
        });

    }



    /**
     * 存储数据到文件
     *
     * @param fileName 文件名 如 121212121.txt(无需路径)
     * @param data     数据
     */
    public void saveDataToFile(String fileName, String data) {
        // 通过线程池存储数据到文件
        ThreadPoolUtils.executeNormalTask(() -> {
            FileOutputStream out = null;
            BufferedWriter writer = null;
            try {
                out = openFileOutput(fileName, Context.MODE_APPEND);
                writer = new BufferedWriter(new OutputStreamWriter(out));
                writer.write(data);
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (writer != null) {
                        writer.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        // 更新界面的手术名称
        etOperationName.setText(AppStatic.collectionBasicInfoEntity.getChoosedOperationName());
    }

    /**
     * 输入验证
     */
    private boolean verifyInput() {

//        // 检查身份证号
//        if (!HealthRecordUtils.verifyPatientId(etPatientId.getText().toString())) {
//            ToastUtil.toast(getApplicationContext(), "输入的身份证号码有误", TastyToast.WARNING);
//            return false;
//        }
//
//        // 检查住院号
//        if (!HealthRecordUtils.verifyAdmissionId(etAdmissionId.getText().toString())) {
//            ToastUtil.toast(getApplicationContext(), "输入的住院号有误", TastyToast.WARNING);
//            return false;
//        }

        // 检查病人年龄
        if (!HealthRecordUtils.verifyAge(etPatientAge.getText().toString())) {
            ToastUtil.toast(getApplicationContext(), "输入的病人年龄不规范", TastyToast.WARNING);
            return false;
        }

        // 检查病人身高
        if (!HealthRecordUtils.verifyPatientWeight(etPatientWeight.getText().toString())) {
            ToastUtil.toast(getApplicationContext(), "输入的病人身高不规范", TastyToast.WARNING);
            return false;
        }

        // 检查病人体重
        if (!HealthRecordUtils.verifyPatientWeight(etPatientWeight.getText().toString())) {
            ToastUtil.toast(getApplicationContext(), "输入的病人体重不规范", TastyToast.WARNING);
            return false;
        }

        // 检查手麻系统手术序列号
        if (!HealthRecordUtils.verifyHospitalOperationNumber(etHospitalOperationNumber.getText().toString())) {
            ToastUtil.toast(getApplicationContext(), "输入的手麻系统手术序列号不规范", TastyToast.WARNING);
            return false;
        }

        return true;
    }

    /**
     * 返回选择的性别
     */
    private String getPatientSex() {
        if (rbPatientSexMan.isChecked()) {
            return "0";
        } else if (rbPatientSexWoman.isChecked()) {
            return "1";
        }
        return "none";
    }


    /**
     * 返回是否为急诊
     */
    private boolean getUrgentInfo() {
        if (rbIsUrgentYes.isChecked()) {
            return true;
        } else {
            return false;
        }
    }


    /**
     * 返回ASA等级的字符串
     *
     * @return ASA等级
     */
    private Integer getASALevel() {
        if (rbASALevel1.isChecked()) {
            return 1;
        } else if (rbASALevel2.isChecked()) {
            return 2;
        } else if (rbASALevel3.isChecked()) {
            return 3;
        } else if (rbASALevel4.isChecked()) {
            return 4;
        } else {
            return 5;
        }
    }

    /**
     * 根据Spinner返回的Index值返回麻醉方式字符串
     *
     * @return 麻醉方式字符串
     */
    private String getAnesthesiaMode() {
        // "麻醉方式", "局麻", "腰麻", "MAC麻醉", "阻滞麻醉", "吸入麻醉", "静吸复合麻醉", "全凭静脉麻醉",  "腰硬联合麻醉", "持续硬膜外麻醉"
        StringBuilder maZuiFangShiString = new StringBuilder();
        if (boxJuMa.isChecked()) {
            maZuiFangShiString.append("局麻 ");
        }
        if (boxYaoMa.isChecked()) {
            maZuiFangShiString.append("腰麻 ");
        }
        if (boxMacMaZui.isChecked()) {
            maZuiFangShiString.append("MAC麻醉 ");
        }
        if (boxZuZhiMaZui.isChecked()) {
            maZuiFangShiString.append("阻滞麻醉 ");
        }
        if (boxXiRuMaZui.isChecked()) {
            maZuiFangShiString.append("吸入麻醉 ");
        }
        if (boxJingXiFuHeMaZui.isChecked()) {
            maZuiFangShiString.append("静吸复合麻醉 ");
        }
        if (boxQuanPingJingMaiMaZui.isChecked()) {
            maZuiFangShiString.append("全凭静脉麻醉 ");
        }
        if (boxYaoYingLianHeMaZui.isChecked()) {
            maZuiFangShiString.append("腰硬联合麻醉 ");
        }
        if (boxChiXuYingMoWaiMaZui.isChecked()) {
            maZuiFangShiString.append("持续硬膜外麻醉 ");
        }
        return maZuiFangShiString.toString().trim();
    }

    /**
     * 为调试方便设置默认的病人信息
     */
    private void setDefaultPatientInfo() {
        collectionBasicInfoEntity.setHospitalArea("重庆");
        collectionBasicInfoEntity.setHospitalLevel("三甲");
        collectionBasicInfoEntity.setHospitalCode("50");
        Random random = new Random();
        collectionBasicInfoEntity.setPatientId("" + System.currentTimeMillis() + random.nextInt(100000000));
        collectionBasicInfoEntity.setAdmissionId("" + random.nextInt(1000000000));
        collectionBasicInfoEntity.setPatientAge("90");
        collectionBasicInfoEntity.setPatientHeight("190");
        collectionBasicInfoEntity.setPatientWeight("70");
        collectionBasicInfoEntity.setPatientSex("1");
        collectionBasicInfoEntity.setOperationName("XXXXXXX术");
        collectionBasicInfoEntity.setHospitalOperationNumber("" + System.currentTimeMillis());
        collectionBasicInfoEntity.setBeforeOperationDiagnosis("普通感冒");
        collectionBasicInfoEntity.setOperationIsUrgent(false);
        collectionBasicInfoEntity.setOperationASALevel(3);
        collectionBasicInfoEntity.setOperationHeartFunctionLevel(1);
        collectionBasicInfoEntity.setOperationLungFunctionLevel(2);
        collectionBasicInfoEntity.setOperationLiverFunctionLevel(1);
        collectionBasicInfoEntity.setOperationKidneyFunctionLevel(0);
        collectionBasicInfoEntity.setOperationAnesthesiaMode("局麻");
        collectionBasicInfoEntity.setPastMedicalHistory("无");
        collectionBasicInfoEntity.setSpecialDiseaseCase("高血压");
        logger.debug(collectionBasicInfoEntity.toString());

        EmrEntity emrEntity = new EmrEntity();
        emrEntity.setPatientId("500281196003027281");
        emrEntity.setAdmissionId("A237892");
        emrEntity.setPatientAge("60");
        emrEntity.setPatientHeight("176cm");
        emrEntity.setPatientWeight("70kg");
        emrEntity.setPatientSex("男");
        emrEntity.setOperationName("胸腔镜下肺修补术");
        emrEntity.setHospitalOperationNumber("AS123890");
        emrEntity.setBeforeOperationDiagnosis("咳嗽3年,肺部病变");
        emrEntity.setOperationIsUrgent("否");
        emrEntity.setOperationASALevel("3级");
        emrEntity.setOperationHeartFunctionLevel("II级");
        emrEntity.setOperationLungFunctionLevel("3级");
        emrEntity.setOperationLiverFunctionLevel("A级");
        emrEntity.setOperationKidneyFunctionLevel("3级");
        emrEntity.setPastMedicalHistory("糖尿病,高血压病史4年,17年进行肾结石手术.");
        emrEntity.setSpecialDiseaseCase("糖尿病，高血压");

        // 缓存这个数据
        AppStatic.emrData = JSON.toJSONString(emrEntity);
    }



}
