<template>
  <section>
    <!--工具条-->
    <el-col :span="24" class="toolbar" style="padding-bottom: 0px;">
      <el-form :inline="true" :model="filters">
        <el-form-item label="病历号：">
          <el-input v-model="filters.medicalRecord_id" placeholder="请输入病历号"></el-input>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" icon="el-icon-search" v-on:click="getPatient()">查询</el-button>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" icon="el-icon-goods" @click="pay" style="margin-left: 600px">收费结算</el-button>
        </el-form-item>
      </el-form>
    </el-col>

    <!--列表-->
    <template>
      <el-table ref="multipleTable" :data="paymentItem" highlight-current-row v-loading="loading" tooltip-effect="dark"
                style="width: 100%;" @selection-change="handleSelectionChange">
        <el-table-column type="selection" width="55"></el-table-column>

        <el-table-column label="病历号" width="80">{{filters.medicalRecord_id}}</el-table-column>

        <el-table-column label="姓名" width="120">{{patient.name}}</el-table-column>

        <el-table-column prop="name" label="项目名称" width="150"></el-table-column>

        <el-table-column prop="price" label="单价" width="120" sortable></el-table-column>

        <el-table-column prop="number" label="数量" width="120" sortable></el-table-column>

        <el-table-column prop="time" label="开立时间" min-width="120" sortable></el-table-column>

        <el-table-column prop="status" label="状态" min-width="120"></el-table-column>

        <el-dialog title="发票信息（交费）" :visible.sync="dialogFormVisible" width="600px" :append-to-body="true" center>
          <el-form :model="paymentForm">
            <el-form-item label="病历号：" :label-width="formLabelWidth">
              <el-input v-model="filters.medicalRecord_id" style="width: 200px"></el-input>
            </el-form-item>
            <el-form-item label="患者姓名：" :label-width="formLabelWidth">
              <el-input v-model="patient.name" style="width: 200px"></el-input>
            </el-form-item>
            <el-form-item label="支付方式" :label-width="formLabelWidth">
              <el-select v-model="paymentForm.methodPay" placeholder="请选择支付方式">
                <el-option value="现金" label="现金">现金</el-option>
                <el-option value="微信" label="微信">微信</el-option>
                <el-option value="支付宝" label="支付宝">支付宝</el-option>
              </el-select>
            </el-form-item>
            <el-form-item label="应收金额：" :label-width="formLabelWidth">
              <el-input v-model="paymentForm.shouldPay" style="width: 200px"></el-input>
            </el-form-item>
            <el-form-item label="实收金额：" :label-width="formLabelWidth">
              <el-input v-model="paymentForm.actualPay" style="width: 200px" @change="calculateChange"></el-input>
            </el-form-item>
            <el-form-item label="找零金额：" :label-width="formLabelWidth">
              <el-input v-model="paymentForm.change" style="width: 200px"></el-input>
            </el-form-item>
          </el-form>
          <div slot="footer" class="dialog-footer">
            <el-button @click="dialogFormVisible = false">取 消</el-button>
            <el-button type="primary" @click="dialogClick">收 费</el-button>
          </div>
        </el-dialog>

      </el-table>
    </template>

  </section>
</template>
<script>
  import axios from "axios";
  import qs from "qs";

  export default {
    data() {
      return {
        filters: {
          medicalRecord_id: '',
          others_id: 1
        },
        patient: {
          patient_id: '',
          name: '',
          idNumber: '',
        },
        loading: false,
        paymentItem: [],
        multipleSelection: null,
        dialogFormVisible: false,
        formLabelWidth: '100px',
        paymentForm: {
          shouldPay: 0,
          actualPay: 0,
          methodPay: '',
          change: 0
        }
      }
    },
    methods: {
      //获取用户病历信息
      getPatient() {
        this.loading = true;
        axios.post('http://localhost:8088/payment/find-patient-info', qs.stringify({
          medicalRecord_id: this.filters.medicalRecord_id
        })).then((response) => {
          if (response.data.result === "success") {
            setTimeout(() => {
              this.patient.patient_id = response.data.data.id;
              this.patient.name = response.data.data.name;
              this.patient.idNumber = response.data.data.idNumber;
              this.getPaymentItem();

              this.$message({
                message: '已获取病历信息！',
                type: 'success'
              });
            }, 1500);
          } else {
            setTimeout(() => {
              this.loading = false;
              this.$message.error("无此病历信息！")
            }, 1500);
          }
        });
      },
      getPaymentItem() {
        axios.post('http://localhost:8088/payment/get-payment-item', qs.stringify({
          patient_id: this.patient.patient_id
        })).then((response) => {
          this.paymentItem = response.data.data;
          this.loading = false;
        });
      },
      handleSelectionChange(val) {
        this.multipleSelection = val;
        console.log(val);
      },
      pay() {
        if (this.multipleSelection == null) {
          this.$message.error('请选择缴费项目！');
        } else {
          this.dialogFormVisible = true;
          this.multipleSelection.forEach(item => {
            this.paymentForm.shouldPay += parseFloat(item.total_price);
          })
        }
      },

      dialogClick() {
        this.dialogFormVisible = false;
        this.multipleSelection.forEach(item => {
          axios.post('http://localhost:8088/payment/pay', qs.stringify({
            paymentItem_id: item.paymentItem_id,
            others_id: this.filters.others_id
          })).then((response) => {
            console.log(response.data);
          });
        });
        this.paymentItem = [];
        this.$message({
          message: '缴费成功！',
          type: 'success'
        });
      },

      calculateChange() {
        this.paymentForm.change = (parseFloat(this.paymentForm.actualPay) - parseFloat(this.paymentForm.shouldPay)).toFixed(1);
      }
    },

  };
</script>
