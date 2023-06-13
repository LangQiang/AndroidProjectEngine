#!/bin/bash

# 使用前提
# 1.配置gradle环境变量
# 2.下载coscmd工具配置环境变量，并配置coscmd的config。也就是说你得有云存储
# 3.安装curl工具 并配置环境变量
# 4.切换到打包项目的app(如果你的主工程叫做app的话)下
# 5.明确两个问题，会配置gradle的打包资源目录和apk生成目录（这个可以忽略）
# e.g. 针对这个工程 在当前目录下执行 sh ./publish-skin-res-apk.sh
#
# 配置文件参数：
# 见publish_skin_res.conf
#
# todo: 此脚本实现在本地机器上发布皮肤插件包，如果在CI或者jenkins上面自动构建，需要改动一下参数接受方式，比如可以读区系统临时环境变量等方式
#

function print_log() {
  local color="\033[0;31m"
  local log="$1"
  echo -e "$color$log"
}

configPath="publish_skin_res.conf" # 替换为你的文件名

function find_value() {
    local search_key=$1
    local value
    value=$(grep -E "^$search_key=" "$configPath" | cut -d'=' -f2)
    value="$(echo -e "${value}" | sed -e 's/^[[:space:]]*//' -e 's/[[:space:]]*$//')"
    echo "${value}"
}


# 初始化参数
default_res_dir="src/main/res"
res_dir="$(find_value 'res_dir')"
res_dir=${res_dir:-$default_res_dir}
print_log "res_dir: $res_dir"

default_apk_dir="build/outputs/apk/debug"
apk_dir=$(find_value 'apk_dir')
apk_dir=${apk_dir:-$default_apk_dir}
print_log "apk_dir: $apk_dir"

default_apk_rename="$(LC_CTYPE=C tr -dc 'a-zA-Z0-9' </dev/urandom | head -c 15).apk"
apk_rename=$(find_value 'apk_rename')
apk_rename=${apk_rename:-$default_apk_rename}
print_log "apk_rename: $apk_rename"

skin_name=$(find_value 'skin_name')
print_log "skin_name: $skin_name"

skin_desc=$(find_value 'skin_desc')
print_log "skin_desc: $skin_desc"

skin_cover=$(find_value 'skin_cover')
print_log "skin_cover: $skin_cover"

skin_version=$(find_value 'skin_version')
print_log "skin_version: $skin_version"

skin_channel=$(find_value 'skin_channel')
print_log "skin_channel: $skin_channel"

skin_category=$(find_value 'skin_category')
print_log "skin_category: $skin_category"

skin_tag=$(find_value 'skin_tag')
print_log "skin_tag: $skin_tag"

skin_type=$(find_value 'skin_type')
print_log "skin_type: $skin_type"

# 检查参数合法性 apk_dir这个目录在打包后检测
if [ ! -d "$res_dir" ] \
  || [ -z "${skin_name}" ] \
  || [ -z "${skin_desc}" ] \
  || [ -z "${skin_cover}" ] \
  || [ -z "${skin_version}" ] \
  || [ -z "${skin_channel}" ] \
  || [ -z "${skin_category}" ] \
  || [ -z "${skin_tag}" ] \
  || [ -z "${skin_type}" ]; then
  print_log "error: 参数错误请检查配置文件"
  exit 1
fi

#提示服务上是否已经存在名字为skin_name的皮肤包
encode_name=$(echo -n "$skin_name" | xxd -ps | tr -d '\n' | sed -r 's/(..)/%\1/g') #这个编码不怎么靠谱
skin_exist=$(curl -s http://43.138.100.114/skin/check/name/"$encode_name" | awk -F'isExist' '{print $2}' | awk -F '"' '{print $3}')
if [ "$skin_exist" = "true" ]; then
  print_log "${skin_name} 已经存在"
  read -r -p "确认覆盖已经存在的皮肤包？(y/n): " choice
  if [[ "$choice" != "y" && "$choice" != "Y" ]]; then
    print_log "取消发布"
    exit 1
  fi
fi

#清空apk_dir
if [ -d "${apk_dir}" ]; then
    mv "${apk_dir}" "${apk_dir}_temp"
fi

#打包
gradle clean assembleDebug -Pres_directory="$res_dir"

#检查apk生成目录是否存在
if [ ! -d "$apk_dir" ]; then
  print_log "error:apk生成目录未设置或不存在"
  exit 1
fi

apk_path=$(find "$apk_dir" -name "*.apk" -type f)
#检查apk文件是否存在
if [ ! -f "$apk_path" ]; then
  print_log "error:${apk_path}文件不存在"
  exit 1
fi

# 重命名文件准备上传cos
upload_file_path="$(dirname "$apk_path")/${apk_rename}"
cp "$apk_path" "$upload_file_path"
print_log "upload_file_path: $upload_file_path"

# 定义 COS 目标存储桶和目录 上传cos
cos_bucket="godq-1307306000"
cos_directory="skin/${apk_rename}"
cos_region="ap-beijing"
coscmd -d -b "$cos_bucket" -r "$cos_region" upload "$upload_file_path" "/$cos_directory"

download_url="https://$cos_bucket.cos.$cos_region.myqcloud.com/$cos_directory"
print_log "download url: $download_url"

#同步服务 先不需要校验后期再加账号权限
url="http://43.138.100.114/skin/create"
request_method="POST"
content_type="content-type:application/json"
# 构建请求体数据
request_data='{"skin_name":"'"$skin_name"'","skin_desc":"'"$skin_desc"'","skin_cover":"'"$skin_cover"'","skin_version":'"$skin_version"',"skin_url":"'"$download_url"'","skin_channel":"'"$skin_channel"'","skin_category":"'"$skin_category"'","skin_tag":"'"$skin_tag"'","skin_type":'"$skin_type"'}'
# 发送请求
curl -X "$request_method" -H "$content_type" -d "$request_data" "$url"

print_log "\nSUCCESSFUL!!!"

print_log "皮肤列表获取接口：http://43.138.100.114/skin/list"