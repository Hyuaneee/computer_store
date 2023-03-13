Vue.config.productionTip = false  //以阻止 vue 在启动时生成生产提示。 <!-- 全局配置 -->

new Vue({
    el: '#app',
    data: {
        //修改按钮是否禁用
        loading: false,
        //倒计时
        showTime: false,   //修改按钮上的倒计时是否显示
        downTime: 0,  //倒计时

        //获取用户信息
        user: {
            uid: 0,
            username: '',
            avatar: ''
        },
        //提交的信息
        formData: {
            username: '',
            phone: '',
            gender: 1,
        },
        //表单规则
        rules: {
            username: [{required: true, message: '用户名不能为空', trigger: 'blur'},
                {min: 2, max: 80, message: '用户名不能小于2位,，大于80位', trigger: 'blur'},
            ],  //blur失去焦点
            phone: [
                {required: true, message: '密码不能为空', trigger: 'blur'},
                {pattern: /^1[3|4|5|7|8|9][0-9]\d{8}$/, message: '请输入正确的手机号', trigger: 'blur'},
            ],

        },
    },
    //钩子函数，VUE对象初始化完成后自动执行
    created() {
        this.getUser();
        this.selectUser();
    },
    methods: {
        //获取用户信息
        getUser() {
            axios({
                method: "get",
                url: "/user/getUser",
            }).then((res) => {
                if (res.data.code === 1) {
                    this.userLoading = true;
                    this.user = res.data.data;
                    this.formData = res.data.data;
                } else {
                    this.userLoading = false;
                }
            });
        },
        //搜索
        searchContent() {
            location.href = "search.html?context=" + this.searchData;
        },
        //实现倒计时
        Time() {
            this.downTime = 30;
            var time = setInterval(() => {
                this.downTime -= 1;
                if (this.downTime == 0) {
                    this.showTime = false;
                    this.loading = false,
                        clearInterval(time);
                }
            }, 1000)
        },
        //修改信息
        update() {
            this.loading = true;
            this.showTime = true;
            this.$refs.formData.validate((valid) => {
                if (valid) {
                    axios({
                        method: "PUT",
                        url: "/user/updateUser",
                        data: {
                            username: this.formData.username,
                            phone: this.formData.phone,
                            gender: this.formData.gender
                        }
                    }).then((res) => {
                        if (res.data.code === 1) {
                            this.$message.success({
                                message: "用户信息修改成功",
                                type: 'success'
                            });
                            window.location.reload();
                        } else {
                            this.$message.error(res.data.message);
                        }
                    });
                } else {
                    this.$message.error('此提交不符合规则');
                    return false;
                }
            });
            this.Time();
            this.formData.phone = '';
        },
    }
});