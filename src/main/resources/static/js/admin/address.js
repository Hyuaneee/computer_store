Vue.config.productionTip = false  //以阻止 vue 在启动时生成生产提示。 <!-- 全局配置 -->
const v = new Vue({
    el: '#app',
    data: {
        loading: false,   //页面加载,false:关闭,true:加载中
        dialogFormVisible: false,   //是否弹出的添加窗口
        dialogUpdateVisible: false,   //是否弹出的修改窗口
        //用户信息
        formData: {},
        //获取用户信息
        user: {
            uid: null,
            username: '',
            avatar: ''
        },
        //分页数据
        page: {
            content: [],
            currentPage: 1,   //当前页
            pageSize: 4,  //当前页条数
            total: 1,  //数据总条数
            pages: 1,  //总页数
            firstPage: false,  //是否禁用前一页按钮
            lastPage: false,  //是否有禁用下一页按钮
        },
        //复选框
        multipleSelection: [],

        //修该和添加规则
        rules: {    //表单规则
            name: [{required: true, message: '不能为空', trigger: 'blur'}],  //blur失去焦点
            provinceName: [{required: true, message: '不能为空', trigger: 'blur'}],
            cityName: [{required: true, message: '不能为空', trigger: 'blur'}],
            areaName: [{required: true, message: '不能为空', trigger: 'blur'}],
            address: [{required: true, message: '地址必填项', trigger: 'blur'}],
            phone: [
                {required: true, message: '手机号不能为空', trigger: 'blur'},
                {pattern: /^1[3|4|5|7|8|9][0-9]\d{8}$/, message: '请输入正确的手机号', trigger: 'blur'},
            ],
            tag: [{max: 6, message: '用户名不能大于6位', trigger: 'blur'}]
        },
        //修改窗口信息
        elAUpdate: {},
        //添加窗口信息
        elAdd: {},

        addressList: {
            provinces: [],  //列表信息,省/直辖市
            citys: [],  //列表信息,城市
            areas: [],  //列表信息,区
        }
    },
    //钩子函数，VUE对象初始化完成后自动执行
    created() {
        this.getUser();
        this.findPage();
    },
    methods: {
        //获取用户信息
        getUser() {
            axios({
                methods: "get",
                url: "/user/getUser",
            }).then((res) => {
                if (res.data.code === 1) {
                    this.userLoading = true;
                    this.user = res.data.data;
                } else {
                    this.userLoading = false;
                }
            });
        },
        //搜索
        searchContent() {
            location.href = "search.html?context=" + this.searchData;
        },
        //获取当前页数据
        findPage() {
            this.loading = true;
            axios({
                method: "POST",
                url: "/address/PageList/" + this.page.currentPage + "/" + this.page.pageSize,
                data: {},
            }).then((res) => {
                this.page.content = res.data.data.records;
                this.page.currentPage = res.data.data.current;
                this.page.pageSize = res.data.data.size;
                this.page.total = res.data.data.total;
                this.page.pages = res.data.data.pages;
                this.page.firstPage = this.page.currentPage == 1 ? true : false;
                this.page.lastPage = this.page.currentPage == this.page.pages ? true : false;
                setTimeout(function () {
                }, 1000)
                this.loading = false;  //关闭加载
            });
        },
        //添加和修改的取消
        cancel() {
            this.dialogFormVisible = false;
            this.dialogUpdateVisible = false;
        },
        //显示修改页面
        handleUpdate(row) {
            axios({
                method: "get",
                url: "/address/selectId/" + row.aid,
            }).then((res) => {
                if (!res.data.code === 1) {
                    this.$message(res.data.data);
                } else {
                    this.elAUpdate = res.data.data;
                    this.dialogUpdateVisible = true;
                    //因为要获取所有信息，列表页要获取到
                    this.getAllProvince();
                }
            }).finally(() => {
                axios({
                    method: "get",
                    url: "/dict_district/getListCity/" + this.elAUpdate.provinceCode,
                }).then((res) => {
                    if (!res.data.code == 1) {
                        this.$message(res.data.data);
                    } else {
                        this.addressList.citys = res.data.data;
                    }
                });
                axios({
                    method: "get",
                    url: "/dict_district/getListArea/" + this.elAUpdate.cityCode,
                }).then((res) => {
                    if (!res.data.code === 1) {
                        this.$message(res.data.data);
                    } else {
                        this.addressList.areas = res.data.data;
                    }
                });
            });
        },
        //提交修改信息
        handleEdit() {
            this.$refs.elAUpdate.validate((valid) => {
                if (valid) {
                    this.$confirm("此操作将从数据库中永久修改数据，是否继续？", "提示", {
                        confirmButtonText: "确定",
                        cancelButtonText: "取消",
                        type: "warning"
                    }).then(() => {   //选择确定的情况
                        this.getuUpdateCode();   //获取code
                        axios({
                            method: "PUT",
                            url: "/address/updateRow",
                            data: this.elAUpdate
                        }).then((res) => {
                            if (res.data.code === 1) {
                                this.$message({
                                    message: res.data.data,
                                    type: "success"
                                });
                            } else {
                                this.$message(res.data.data);
                            }
                        }).finally(() => {
                            this.findPage();
                        });
                    }).catch(() => {     //选择取消的情况
                        this.$message({
                            type: "into",
                            message: "已取消修改"
                        });
                    }).finally(() => {
                        this.dialogUpdateVisible = false;
                    });
                } else {
                    this.$message.error('此提交不符合规则');
                    return false;
                }
            });
        },
        //显示添加页面
        handleAdd(row) {
            this.dialogFormVisible = true;
            this.getAllProvince();  //获取省列表信息
        },
        //提交添加信息
        handleSave() {
            this.$refs.elAdd.validate((valid) => {
                if (valid) {
                    this.$confirm("此操作将从数据库中永久修改数据，是否继续？", "提示", {
                        confirmButtonText: "确定",
                        cancelButtonText: "取消",
                        type: "warning"
                    }).then(() => {   //选择确定的情况
                        this.getAddCode();  //获取code
                        axios({
                            method: "POST",
                            url: "/address/addRow",
                            data: this.elAdd
                        }).then((res) => {
                            if (res.data.code === 1) {
                                this.$message({
                                    message: res.data.message,
                                    type: "success"
                                });
                            } else {
                                this.$message(res.data.message);
                            }
                        }).finally(() => {
                            this.findPage();
                        });
                    }).catch(() => {     //选择取消的情况
                        this.$message({
                            type: "into",
                            message: "已取消添加"
                        });
                    }).finally(() => {
                        this.dialogFormVisible = false;
                    });
                } else {
                    this.$message.error('此提交不符合规则');
                    return false;
                }
            });

        },
        //行删除数据
        rowDelete(row) {
            this.$confirm("此操作将从数据库中永久删除数据，是否继续？", "提示", {
                confirmButtonText: "确定",
                cancelButtonText: "取消",
                type: "warning"
            }).then(() => {   //选择确定的情况
                axios({
                    method: "delete",
                    url: "/address/deleteRow/" + row.aid,
                }).then((res) => {
                    if (res.data.code === 1) {
                        this.$message({
                            message: res.data.message,
                            type: "success"
                        });
                    } else {
                        this.$message(res.data.message);
                    }
                }).finally(() => {
                    this.findPage();
                });
            }).catch(() => {     //选择取消的情况
                this.$message({
                    type: "into",
                    message: "已取消删除"
                });
            });
        },
        //多行删除
        delAll() {
            this.$confirm("此操作将从数据库中永久删除数据，是否继续？", "提示", {
                confirmButtonText: "确定",
                cancelButtonText: "取消",
                type: "warning"
            }).then(() => {   //选择确定的情况
                axios({
                    method: "POST",
                    url: "/address/delete",
                    data: this.multipleSelection
                }).then((res) => {
                    console.log(res);
                    if (res.data.code === 0) {
                        this.$message(res.data.message);
                    } else {
                        this.$message({
                            message: res.data.message,
                            type: "success"
                        });
                    }
                }).finally(() => {
                    this.findPage();
                });

            }).catch(() => {     //选择取消的情况
                this.$message({
                    type: "into",
                    message: "已取消删除"
                });
            });
        },
        //多选
        handleSelectionChange(rows) {
            this.multipleSelection = rows;
        },
        //上一页
        Previous() {
            this.page.currentPage = this.page.currentPage - 1;
            this.findPage();
        },
        //下一页
        Next() {
            this.page.currentPage = this.page.currentPage + 1;
            this.findPage();
        },
        //设置是否默认：0-不默认，1-默认
        setDefault(row) {
            axios({
                method: "get",
                url: "/address/setDefault/" + row.aid,
            }).then((res) => {
                if (!res.data.code === 1) {
                    this.$message(res.data.message);
                } else {
                    this.$message({
                        message: "默认地址设置成功！",
                        type: "success"
                    });
                }
            }).finally(() => {
                this.findPage();
            });
        },
        //重置输入框
        reset() {
            this.$refs.elAdd.resetFields();
        },
        //获取所有的省/直辖市
        getAllProvince() {
            axios({
                method: "get",
                url: "/dict_district/getAllProvince",
            }).then((res) => {
                if (!res.data.code === 1) {
                    this.$message(res.data.message);
                } else {
                    this.addressList.provinces = res.data.data;
                }
            });
        },
        //获取符合条件的城市
        getListCity(name) {
            var parent = null;
            for (province of this.addressList.provinces) {
                if (province.name == name) {
                    parent = province.code;
                }
            }
            axios({
                method: "get",
                url: "/dict_district/getListCity/" + parent,
            }).then((res) => {
                if (!res.data.code === 1) {
                    this.$message(res.data.message);
                } else {
                    this.addressList.citys = res.data.data;
                }
            });
        },
        //获取符合条件的区/县
        getListArea(name) {
            var parent = null;
            for (city of this.addressList.citys) {
                if (city.name == name) {
                    parent = city.code;
                }
            }
            axios({
                method: "get",
                url: "/dict_district/getListArea/" + parent,
            }).then((res) => {
                if (!res.data.code === 1) {
                    this.$message(res.data.message);
                } else {
                    this.addressList.areas = res.data.data;
                }
            });
        },
        //修改的code获取
        getuUpdateCode() {
            for (province of this.addressList.provinces) {
                if (province.name == this.elAUpdate.provinceName) {
                    this.elAUpdate.provinceCode = province.code;
                }
            }
            for (city of this.addressList.citys) {
                if (city.name == this.elAUpdate.cityName) {
                    this.elAUpdate.cityCode = city.code;
                }
            }
            for (area of this.addressList.areas) {
                if (area.name == this.elAUpdate.areaName) {
                    this.elAUpdate.areaCode = area.code;
                }
            }
        },
        //添加的code获取
        getAddCode() {
            for (province of this.addressList.provinces) {
                if (province.name == this.elAdd.provinceName) {
                    this.elAdd.provinceCode = province.code;
                }
            }
            for (city of this.addressList.citys) {
                if (city.name == this.elAdd.cityName) {
                    this.elAdd.cityCode = city.code;
                }
            }
            for (area of this.addressList.areas) {
                if (area.name == this.elAdd.areaName) {
                    this.elAdd.areaCode = area.code;
                }
            }
        }
    }
});