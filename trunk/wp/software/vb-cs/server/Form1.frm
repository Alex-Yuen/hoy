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
   StartUpPosition =   2  '��Ļ����
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
      Caption         =   "�ر��˳�"
      Height          =   435
      Left            =   4200
      TabIndex        =   1
      Top             =   2040
      Width           =   975
   End
   Begin VB.CommandButton Command1 
      Caption         =   "��������"
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
      Caption         =   "ϵͳ����"
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

'��ǰsocket�Ƿ��Ѿ�ʹ��
'socket�������������ֵ��������Ҫ�ʵ����ߴ���ֵ������֧�ָ����ͬʱ�����û���
Dim status(100) As String

Dim sks(2000, 5) As Variant  'sid, username, msg, last_req_time, rsa-d, rsa-n   '��¼2000�����ӣ�sid ������

Private Sub Command1_Click()
    If sockServer(0).State <> sckClosed Then sockServer(0).Close   '�ж��Ƿ��ڼ���״̬,������ڼ���״̬,��ô�رտؼ�
    sockServer(0).LocalPort = "1228"     '���ü����˿�Ϊ1228
    sockServer(0).Listen                 '��ʼ����
    StateInfo.Caption = "��ʼ����"          '��TEXT�ؼ�����ʾ״̬
    Me.SockList.AddItem "sockserver(0) ��ʼ����"  '��ӵ��¼��б�
    Command1.Enabled = False           '���Ŀ�������ť��״̬
    Command2.Enabled = True           '���Ĺر��˳���ť��״̬
End Sub
                                                                         
Private Sub Command2_Click()
    Unload Me                     '�رմ���,�ر�����ؼ��Ĵ������FROM_UNLOAD��
End Sub
                                                                         
Private Sub Form_Load()
    Set util = New util
    Set rsa = New rsa
End Sub

Private Sub Form_Unload(Cancel As Integer)
    If sockServer(0).State <> sckClosed Then                    '�жϷ��������Ƿ�ر�
        sockServer(0).Close                                     '�رշ��������
    End If
End Sub
                                                                    
Private Sub sockServer_Close(Index As Integer)
    SockList.AddItem "�ͻ���:" & sockServer(Index).RemoteHostIP & "�Ѿ��ر�����"     '��ӿͻ��˹رյ��¼��б�
    status(Index) = ""                              '���������״̬Ϊ�ر�
    Unload sockServer(Index)                         '�ͷ���Ӧ��SOCK�ؼ�
    'Exit Sub
End Sub

Private Sub sockServer_ConnectionRequest(Index As Integer, ByVal requestID As Long)   '�������յ����������
    Dim i As Integer, pi As Integer
    Dim bi As Boolean
    If Index = 0 Then
        For i = 1 To 30                            'ѭ����������,�ж��Ƿ������ӿ���
            If status(i) = "" Then            '������û��װ�صĿؼ�ʱ
                pi = i                     '��¼�ؼ����
                bi = True                  '��¼�п��ÿؼ�
                Exit For
            End If
        Next i
        
        If bi = True Then              '���п��ÿؼ�ʱ
            status(pi) = sockServer(0).RemoteHostIP          '���ͻ���IP��¼������
            Load sockServer(pi)                               'װ����Ӧ�Ŀؼ�
            sockServer(pi).LocalPort = 10000 + pi             '�������Ӷ˿�Ϊ10000+�ؼ����
            sockServer(pi).Accept requestID                   '��Ӧ����������������
            SockList.AddItem "�ͻ���:" & sockServer(pi).RemoteHostIP & "�Ѿ�����"    '��ӵ��¼��б�
        Else
            MsgBox "�ͻ�����������"           '�������������������ʾ��Ϣ
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
    Dim content As String   '��������
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
        For i = 1 To 2000                            'ѭ����������,�ж��Ƿ��м�¼����
            If sks(i, 0) = Empty Then            '������û��װ�صĿؼ�ʱ
                pi = i                    '��¼�ؼ����
                bi = True                  '��¼�п��ÿؼ�
                Exit For
            End If
        Next i
           ' SockList.AddItem "2"
        If bi = True Then
            sks(pi, 0) = sid
            sks(pi, 1) = Empty   'no msg
            sks(pi, 2) = Empty   'no user
            sks(pi, 3) = GetTickCount()    '������ʱ��
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
                For i = 1 To 2000                            'ѭ����������,�ж��Ƿ��м�¼����
                    If sks(i, 0) = sid Then            '������û��װ�صĿؼ�ʱ
                        pi = i                    '��¼�ؼ����
                        bi = True                  '��¼�п��ÿؼ�
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
                        For i = 1 To 2000                            'ѭ����������,�ж��Ƿ��Ѿ���¼
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
                    
                    sks(pi, 3) = GetTickCount()    '������ʱ��
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
    fd = "`!" '������ݰ�������־
    ReDim Preserve data(dl + 4)                                          '���ƻ�ԭ�����ݵ�������ض���ԭ�ж���������
    CopyMemory data(dl + 1), fd(0), 4
 '   SockList.AddItem "6"
    sockServer(Index).SendData data                                                 ' �ͻؿͻ���
             
    DoEvents                                                                          'ת�ƿ���Ȩ,�Դ��������ͻ��˵���Ӧ
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



