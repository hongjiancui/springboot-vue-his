<template>
  <section>
    <!--工具条-->
    <el-col :span="24" class="toolbar" style="padding-bottom: 0;">
      <el-form :inline="true" :model="filters">
        <el-form-item label="病历号：">
          <el-input v-model="filters.medicalRecord_id" placeholder="请输入病历号"></el-input>
        </el-form-item>
        <el-form-item label="时间">
          <el-date-picker type="date" placeholder="选择日期" style="width: 100%;" v-model="filters.date"
                          value-format="yyyy-MM-dd" format="yyyy-MM-dd"></el-date-picker>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" icon="el-icon-search" v-on:click="getDrug()">查询</el-button>
        </el-form-item>
      </el-form>
    </el-col>

    <!--列表-->
    <template>
      <el-tag style="padding: 0; width: 100%;height: 40px">
        <el-button type="primary" icon="el-icon-success" style="float: left" @click="dispenseDrug">发药</el-button>
      </el-tag>
      <el-table ref="multipleTable" :data="drugItem" highlight-current-row v-loading="loading" tooltip-effect="dark"
                style="width: 100%;" @selection-change="handleSelectionChange">
        <el-table-column type="selection" width="55"></el-table-column>

        <el-table-column prop="drug_name" label="药品名称" width="400"></el-table-column>

        <el-table-column prop="number" label="数量" width="120"></el-table-column>

        <el-table-column prop="status" label="药品状态" min-width="120"></el-table-column>

        <el-table-column prop="prescription_name" label="处方名称" min-width="120"></el-table-column>

        <el-table-column label="开立时间" min-width="120">{{this.filters.date}}</el-table-column>
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
          date: '',
        },
        loading: false,
        drugItem: [],
        multipleSelection: null,
      }
    },
    methods: {
      //获取用户病历信息
      getDrug() {
        this.loading = true;
        axios.post('http://localhost:8088/dispense-drug/get-medicine', qs.stringify({
          medicalRecord_id: this.filters.medicalRecord_id,
          date: this.filters.date
        })).then((response) => {
          setTimeout(() => {
            this.drugItem = response.data.data;
            this.$message({
              message: '已获取药品信息！',
              type: 'success'
            });
            this.loading = false;
          }, 1500);
        });
      },
      handleSelectionChange(val) {
        this.multipleSelection = val;
      },
      dispenseDrug() {
        axios.post('http://localhost:8088/dispense-drug', qs.stringify({
          medicalRecord_id: this.filters.medicalRecord_id
        })).then(() => {
          this.getDrug();
          this.$message({
            message: '已发放药品！',
            type: 'success'
          });
        });
      }
    }
  };
</script>
