Vue.config.productionTip = false  //以阻止 vue 在启动时生成生产提示。 <!-- 全局配置 -->
new Vue({
    el: '#app',
    data: {
        loading: false,    //页面加载,false:关闭,true:加载中
        patternName: true,
        //倒计时
        showTime: false,
        downTime: 0,
        //用户信息
        formUser: {
            username: '',
            password: '',
        },
        //表单规则
        rules: {
            username: [{required: true, message: '管理员账号不能为空', trigger: 'blur'},
                {min: 2, max: 80, message: '管理员账号不能小于2位,，大于80位', trigger: 'blur'},
            ],  //blur失去焦点
            password: [{required: true, message: '密码不能为空', trigger: 'blur'},
                {pattern: /^[ a-zA-Z0-9]{6,30}$/, message: '密码不能小于6位,不能大于30位,无特殊字符', trigger: 'blur'},
            ],
        }
    },
    methods: {
        //实现倒计时
        Time() {
            this.downTime = 30;
            var time = setInterval(() => {
                this.downTime -= 1;
                if (this.downTime == 0) {
                    this.showTime = false;
                    clearInterval(time);
                }
            }, 1000)
        },
        //用户登录
        loginData() {
            this.$refs.formUser.validate((valid) => {
                if (valid) {
                    axios({
                        method: "POST",
                        url: "/admin/login",
                        data: {
                            username: this.formUser.username,
                            password: this.formUser.password
                        },

                        //contentType: "application/json;charset=utf-8"
                    }).then((res) => {
                        if (res.data.code == 0) {
                            this.$message.error(res.data.message);
                        } else {
                            this.$message.success({
                                message: "登录成功，即将跳转到后台管理界页！",
                                type: 'success'
                            });
                            setTimeout(function () {
                                location.href = "../page/backend/userlist.html";
                            }, 500)

                        }
                    });
                } else {
                    this.$message.error('此提交不符合规则');
                    return false;
                }
            });

        }
    }
});