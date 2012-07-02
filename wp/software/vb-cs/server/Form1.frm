VERSION 5.00
Object = "{248DD890-BB45-11CF-9ABC-0080C7E7B78D}#1.0#0"; "MSWINSCK.OCX"
Begin VB.Form Form1 
   BackColor       =   &H80000005&
   Caption         =   "Server"
   ClientHeight    =   2955
   ClientLeft      =   60
   ClientTop       =   450
   ClientWidth     =   5850
   LinkTopic       =   "Form1"
   ScaleHeight     =   2955
   ScaleWidth      =   5850
   StartUpPosition =   2  '屏幕中心
   Begin VB.Timer Timer1 
      Interval        =   60000
      Left            =   3720
      Top             =   2280
   End
   Begin VB.ListBox SockList 
      BackColor       =   &H8000000A&
      Height          =   2760
      Left            =   120
      TabIndex        =   2
      Top             =   120
      Width           =   3375
   End
   Begin VB.CommandButton Command2 
      Caption         =   "关闭退出"
      Height          =   435
      Left            =   4200
      TabIndex        =   1
      Top             =   2040
      Width           =   975
   End
   Begin VB.CommandButton Command1 
      Caption         =   "开启服务"
      Height          =   435
      Left            =   4200
      TabIndex        =   0
      Top             =   1200
      Width           =   975
   End
   Begin MSWinsockLib.Winsock sockServer 
      Index           =   0
      Left            =   2760
      Top             =   1920
      _ExtentX        =   741
      _ExtentY        =   741
      _Version        =   393216
   End
   Begin VB.Label StateInfo 
      BackColor       =   &H80000005&
      BackStyle       =   0  'Transparent
      Caption         =   "系统启动"
      Height          =   375
      Left            =   3720
      TabIndex        =   3
      Top             =   240
      Width           =   1695
   End
End
Attribute VB_Name = "Form1"
Attribute VB_GlobalNameSpace = False
Attribute VB_Creatable = False
Attribute VB_PredeclaredId = True
Attribute VB_Exposed = False
Option Explicit

Private Declare Sub CopyMemory Lib "kernel32" Alias "RtlMoveMemory" (Destination As Byte, Source As Byte, ByVal Length As Long)
Private Declare Function GetTickCount Lib "kernel32" () As Long
Private util As util
Private rsa As rsa

'当前socket是否已经使用
'socket并发连接数最大值，根据需要适当调高此数值，可以支持更多的同时连接用户数
Dim status(100) As String

Dim sks(2000, 5) As Variant  'sid, username, msg, last_req_time, rsa-d, rsa-n   '记录2000个连接，sid 短连接

Private Sub Command1_Click()
    If sockServer(0).State <> sckClosed Then sockServer(0).Close   '判断是否处于监听状态,如果处于监听状态,那么关闭控件
    sockServer(0).LocalPort = "1228"     '设置监听端口为1228
    sockServer(0).Listen                 '开始监听
    StateInfo.Caption = "开始侦听"          '在TEXT控件中显示状态
    Me.SockList.AddItem "sockserver(0) 开始侦听"  '添加到事件列表
    Command1.Enabled = False           '更改开启服务按钮的状态
    Command2.Enabled = True           '更改关闭退出按钮的状态
End Sub
                                                                         
Private Sub Command2_Click()
    Unload Me                     '关闭窗体,关闭网络控件的代码放在FROM_UNLOAD中
End Sub
                                                                         
Private Sub Form_Load()
    Set util = New util
    Set rsa = New rsa
End Sub

Private Sub Form_Unload(Cancel As Integer)
    If sockServer(0).State <> sckClosed Then                    '判断服务器端是否关闭
        sockServer(0).Close                                     '关闭服务端连接
    End If
End Sub
                                                                    
Private Sub sockServer_Close(Index As Integer)
    SockList.AddItem "客户端:" & sockServer(Index).RemoteHostIP & "已经关闭连接"     '添加客户端关闭到事件列表
    status(Index) = ""                              '更改数组的状态为关闭
    Unload sockServer(Index)                         '释放相应的SOCK控件
    'Exit Sub
End Sub

Private Sub sockServer_ConnectionRequest(Index As Integer, ByVal requestID As Long)   '服务器收到连接请求后
    Dim i As Integer, pi As Integer
    Dim bi As Boolean
    If Index = 0 Then
        For i = 1 To 30                            '循环整个数组,判断是否有连接可用
            If status(i) = "" Then            '搜索到没有装载的控件时
                pi = i                     '记录控件序号
                bi = True                  '记录有可用控件
                Exit For
            End If
        Next i
        
        If bi = True Then              '当有可用控件时
            status(pi) = sockServer(0).RemoteHostIP          '将客户端IP记录到数组
            Load sockServer(pi)                               '装载相应的控件
            sockServer(pi).LocalPort = 10000 + pi             '设置连接端口为10000+控件序号
            sockServer(pi).Accept requestID                   '回应连接请求并允许连接
            SockList.AddItem "客户端:" & sockServer(pi).RemoteHostIP & "已经连接"    '添加到事件列表
        Else
            MsgBox "客户端连接已满"           '连接数已满的情况下显示消息
        End If
    End If
End Sub

Private Sub sockServer_DataArrival(Index As Integer, ByVal bytesTotal As Long)
    Dim cmd As String
    Dim cmds() As String
    Dim keys As Variant
    
    Dim sid As String
    Dim i As Integer
    Dim pi As Integer
    Dim bi As Boolean
    Dim content As String   '返回内容
    Dim data() As Byte
        
    sockServer(Index).GetData cmd
    'MsgBox cmd
    If (cmd = "*") Then 'request sid & key
        Debug.Print "->" & cmd
        sid = util.genStr
        rsa.GenKey
        keys = rsa.GetKey
        'SockList.AddItem "1"
        'put into sks array
        For i = 1 To 2000                            '循环整个数组,判断是否有记录可用
            If sks(i, 0) = Empty Then            '搜索到没有装载的控件时
                pi = i                    '记录控件序号
                bi = True                  '记录有可用控件
                Exit For
            End If
        Next i
           ' SockList.AddItem "2"
        If bi = True Then
            sks(pi, 0) = sid
            sks(pi, 1) = Empty   'no msg
            sks(pi, 2) = Empty   'no user
            sks(pi, 3) = GetTickCount()    '最后更新时间
            sks(pi, 4) = keys(1)
            sks(pi, 5) = keys(2)
           ' SockList.AddItem "3"
            content = "*/HL.SP/1" & "/HL.SP/" & sid & "/HL.SP/" & keys(0) & "/HL.SP/" & keys(2)
        Else
            content = "*/HL.SP/0"
        End If
            
    Else
        cmds() = Split(cmd, "/HL.SP/")
        sid = cmds(0)
        
        For i = 1 To 2000
            If sks(i, 0) = sid Then
                pi = i
                bi = True
                Exit For
            End If
        Next i
        
        If (bi) Then
            cmd = rsa.Decode(cmds(1), sks(pi, 4), sks(pi, 5))
            Debug.Print "->" & sid & "/HL.SP/" & cmd
            cmds() = Split(cmd, "/HL.SP/")

            If (cmds(0) = "01") Then 'request Login
                Dim result As Boolean
                result = False
                                
                'check if record
                For i = 1 To 2000                            '循环整个数组,判断是否有记录可用
                    If sks(i, 0) = sid Then            '搜索到没有装载的控件时
                        pi = i                    '记录控件序号
                        bi = True                  '记录有可用控件
                        Exit For
                    End If
                Next i
                
                If (bi) Then
                    On Error GoTo abc:
                    'handle login
                    Dim bytData() As Byte
                    Dim objHTTP As Object
                      
                    Set objHTTP = CreateObject("MSXML2.XMLHTTP")
                    'Debug.Print "http://localhost/member.php?mod=logging&action=login&loginsubmit=yes&username=" & cmds(2) & "&password=" & cmds(3)
                    objHTTP.Open "GET", "http://localhost/member.php?mod=logging&action=login&loginsubmit=yes&username=" & cmds(1) & "&password=" & cmds(2), False
                    objHTTP.send
                    
                    If objHTTP.status = 200 Then
                        bytData = objHTTP.responseBody
                        'Debug.Print StrConv(bytData, vbUnicode)
                        Dim ctx As String
                        Dim idx As Long
                        Dim formhash As String
                        
                        ctx = StrConv(bytData, vbUnicode)
                        'Debug.Print ctx
                        idx = InStr(ctx, "action=logout&amp;formhash=")
                        'Debug.Print Mid(ctx, idx + 27, 8)
                        
                        If (idx <> 0) Then    'logout
                            formhash = Mid(ctx, idx + 27, 8)
                            objHTTP.Open "GET", "http://localhost/member.php?mod=logging&action=logout&formhash=" & formhash, False
                            objHTTP.send
                            result = True
                        Else
                            result = False
                        End If
                    Else
                        result = False
                        
                    End If
                    
                    Set objHTTP = Nothing
abc:
                    
                    If (result) Then
                        SockList.AddItem cmds(1) & " logined"
                        'check if exist
                        For i = 1 To 2000                            '循环整个数组,判断是否已经登录
                            If sks(i, 1) = cmds(1) Then
                                sks(i, 2) = "A" 'warning to logout
                                Exit For
                            End If
                        Next i
                        
                        content = cmds(0) & "/HL.SP/1"
                        sks(pi, 1) = cmds(1)
                        sks(pi, 3) = GetTickCount()
                    Else
                        content = cmds(0) & "/HL.SP/0"
                    End If
                                  
                Else
                    content = cmds(0) & "/HL.SP/0"   'login failure
                End If
        
            ElseIf (cmds(0) = "02") Then    'refresh
                'check if record
                For i = 1 To 2000
                    If sks(i, 0) = sid Then
                        pi = i
                        bi = True
                        Exit For
                    End If
                Next i
                
                'not need to decrypt
                
                If (bi) Then
                    If (sks(pi, 2) = Empty) Then
                        content = cmds(0) & "/HL.SP/1"
                    Else
                        content = cmds(0) & "/HL.SP/" & sks(pi, 2)
                    End If
                    
                    sks(pi, 3) = GetTickCount()    '最后更新时间
                Else
                    content = cmds(0) & "/HL.SP/0"   'refresh failure
                End If
            End If
        Else
            content = "*/HL.SP/0"
        End If
    End If
    
    Debug.Print "<-" & content
  '  SockList.AddItem "4"
    'send
    If (cmd = "*") Then
        data() = content
    Else
        data() = rsa.Encode(content, sks(pi, 4), sks(pi, 5))   'crypt
        
        If (sks(pi, 2) = "A") Then  'need to quit, and this array must be clear
            sks(pi, 0) = Empty
            sks(pi, 1) = Empty
            sks(pi, 2) = Empty
            sks(pi, 3) = Empty
            sks(pi, 4) = Empty
            sks(pi, 5) = Empty
        End If
    End If
 '   SockList.AddItem "5"
    Dim dl As Long
    Dim fd() As Byte
    
    dl = UBound(data)
    fd = "`!" '添加数据包结束标志
    ReDim Preserve data(dl + 4)                                          '不破坏原有数据的情况下重定义原有二进制数组
    CopyMemory data(dl + 1), fd(0), 4
 '   SockList.AddItem "6"
    sockServer(Index).SendData data                                                 ' 送回客户段
             
    DoEvents                                                                          '转移控制权,以处理其它客户端的响应
End Sub

Private Sub Timer1_Timer()
    'Debug.Print "timer_server"
    Dim i As Integer
    For i = 1 To 2000
        If ((sks(i, 0) <> Empty)) Then
            If ((GetTickCount() - sks(i, 3)) / 1000 > 60) Then '60s
                sks(i, 0) = Empty
                sks(i, 1) = Empty
                sks(i, 2) = Empty
                sks(i, 3) = Empty
                sks(i, 4) = Empty
                sks(i, 5) = Empty
            End If
        End If
    Next i
End Sub



