//
// Created by  Du Shengzhe on 2017/6/22.
//

#include <jni.h>
#include <string>
#include <sstream>
#include <android/log.h>

using namespace std;

enum Token_value{
    NUMBER,END,PLUS='+',MINUS='-',MUL='*',DIV='/',PRINT=';'
};

Token_value curr_tok=PRINT;
double number_value;
string error_value = "";
int no_of_errors;
stringstream form;

double expr(bool get);
double term(bool get);
double prim(bool get);
Token_value get_token();

extern "C"
JNIEXPORT jstring JNICALL
Java_com_hitiot_dusz7_mtdex_ex1_CalculatorActivity_calculateResultFromJNI(
        JNIEnv *env,
        jobject /* this */,
        jstring formula) {

    const char* str;
    str = env->GetStringUTFChars(formula, false);
    if(str == NULL) {
        return NULL;
    }

    curr_tok = PRINT;
    no_of_errors = 0;
    number_value = 0.0;
    error_value = "";
    form.clear();

    double result = 0.0;
    form << str;
    char test;
    while (form) {
        get_token();
        if (curr_tok==END) break;
        if (curr_tok==PRINT) continue;
        result = expr(false);
    }

    //释放资源
    env->ReleaseStringUTFChars(formula, str);

    if(no_of_errors != 0) {
        return env->NewStringUTF(error_value.c_str());
    }
    ostringstream temp;
    temp << result;
    return env->NewStringUTF(temp.str().c_str());

}




double error(const string& s)
{
    no_of_errors++;
    error_value = "error:" + s;
    return 1;
}


Token_value get_token()
{
    char ch=0;
    form>>ch;
    switch (ch) {
        case 0:
            return curr_tok=END;
        case ';':case '*':case '/':case '+':case '-':case '=':
            return curr_tok=Token_value(ch);
        case '0':case '1':case '2':case '3':case '4':case '5':case '6':case '7':case '8':case '9':case '.':
            form.putback(ch);
            form>>number_value;
            return curr_tok=NUMBER;
        default:
            error("bad token");
            return curr_tok=PRINT;
    }
}



double prim(bool get)
{
    if (get) get_token();
    switch (curr_tok) {
        case NUMBER:
        {   double v=number_value;
            get_token();
            return v;
        }
        case MINUS:
            return -prim(true);
        default:
            return error("primary expected");
    }
}

double term(bool get)
{
    double left=prim(get);
    for (;;)
        switch (curr_tok) {
            case MUL:
                left*=prim(true);
                break;
            case DIV:
                if (double d=prim(true)) {
                    left/=d;
                    break;
                }
                return error("divide by 0");
            default:
                return left;
        }
}

double expr(bool get)
{
    double left=term(get);
    for(;;)
        switch(curr_tok) {
            case PLUS:
                left+=term(true);
                break;
            case MINUS:
                left-=term(true);
                break;
            default:
                return left;
        }
}

