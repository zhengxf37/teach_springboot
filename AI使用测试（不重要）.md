图片：

能够理解图片内容，包括图片文字、图片颜色和物体形状等内容，一次1000token

![image-20250529234327811](C:\Users\89652\AppData\Roaming\Typora\typora-user-images\image-20250529234327811.png)

```python
import os
import base64

from openai import OpenAI

client = OpenAI(
    api_key="sk-XjoyTq1qHChDrfdC9eomdHZ8tHoKF1uUR9ev1OLtVTdn31I4",
    base_url="https://api.moonshot.cn/v1",
)

# 在这里，你需要将 kimi.png 文件替换为你想让 Kimi 识别的图片的地址
image_path = "photo1.jpg"

with open(image_path, "rb") as f:
    image_data = f.read()

# 我们使用标准库 base64.b64encode 函数将图片编码成 base64 格式的 image_url
image_url = f"data:image/{os.path.splitext(image_path)[1]};base64,{base64.b64encode(image_data).decode('utf-8')}"

completion = client.chat.completions.create(
    model="moonshot-v1-8k-vision-preview",
    messages=[
        {"role": "system", "content": "你是 Kimi。"},
        {
            "role": "user",
            # 注意这里，content 由原来的 str 类型变更为一个 list，这个 list 中包含多个部分的内容，图片（image_url）是一个部分（part），
            # 文字（text）是一个部分（part）
            "content": [
                {
                    "type": "image_url",  # <-- 使用 image_url 类型来上传图片，内容为使用 base64 编码过的图片内容
                    "image_url": {
                        "url": image_url,
                    },
                },
                {
                    "type": "text",
                    "text": "请描述图片的内容。",  # <-- 使用 text 类型来提供文字指令，例如“描述图片内容”
                },
            ],
        },
    ],
)

print(completion.choices[0].message.content)
```



文字：

具有让模型获取文件中的信息作为上下文，可识别图片文字转str上传

![image-20250529234442236](C:\Users\89652\AppData\Roaming\Typora\typora-user-images\image-20250529234442236.png)

```python
from typing import *

import os
import json
from pathlib import Path

from openai import OpenAI

client = OpenAI(
    api_key="sk-XjoyTq1qHChDrfdC9eomdHZ8tHoKF1uUR9ev1OLtVTdn31I4",  # 在这里将 MOONSHOT_API_KEY 替换为你从 Kimi 开放平台申请的 API Key
    base_url="https://api.moonshot.cn/v1",
)


def upload_files(files: List[str]) -> List[Dict[str, Any]]:
    """
    upload_files 会将传入的文件（路径）全部通过文件上传接口 '/v1/files' 上传，并获取上传后的
    文件内容生成文件 messages。每个文件会是一个独立的 message，这些 message 的 role 均为
    system，Kimi 大模型会正确识别这些 system messages 中的文件内容。

    :param files: 一个包含要上传文件的路径的列表，路径可以是绝对路径也可以是相对路径，请使用字符串
        的形式传递文件路径。
    :return: 一个包含了文件内容的 messages 列表，请将这些 messages 加入到 Context 中，
        即请求 `/v1/chat/completions` 接口时的 messages 参数中。
    """
    messages = []

    # 对每个文件路径，我们都会上传文件并抽取文件内容，最后生成一个 role 为 system 的 message，并加入
    # 到最终返回的 messages 列表中。
    for file in files:
        file_object = client.files.create(file=Path(file), purpose="file-extract")
        file_content = client.files.content(file_id=file_object.id).text
        messages.append({
            "role": "system",
            "content": file_content,
        })

    return messages


def main():
    file_messages = upload_files(files=["requirements.txt"])
    
    messages = [
        # 我们使用 * 语法，来解构 file_messages 消息，使其成为 messages 列表的前 N 条 messages。
        *file_messages,
        {
            "role": "system",
            "content": "你是 Kimi，由 Moonshot AI 提供的人工智能助手，你更擅长中文和英文的对话。你会为用户提供安全，有帮助，"
                       "准确的回答。同时，你会拒绝一切涉及恐怖主义，种族歧视，黄色暴力等问题的回答。Moonshot AI 为专有名词，不"
                       "可翻译成其他语言。",
        },
        {
            "role": "user",
            "content": "总结一下这些文件的内容。",
        },
    ]

    print(json.dumps(messages, indent=2, ensure_ascii=False))

    completion = client.chat.completions.create(
        model="moonshot-v1-128k",
        messages=messages,
    )

    print(completion.choices[0].message.content)


if __name__ == '__main__':
    main()
```

