VERSION 5.00
Object = "{248DD890-BB45-11CF-9ABC-0080C7E7B78D}#1.0#0"; "MSWINSCK.OCX"
Begin VB.Form Form1 
   Caption         =   "Client"
   ClientHeight    =   3030
   ClientLeft      =   120
   ClientTop       =   450
   ClientWidth     =   4560
   LinkTopic       =   "Form1"
   ScaleHeight     =   3030
   ScaleWidth      =   4560
   StartUpPosition =   2  '��Ļ����
   Begin VB.Timer Timer1 
      Interval        =   5000
      Left            =   600
      Top             =   2040
   End
   Begin MSWinsockLib.Winsock sockClient 
      Left            =   3720
      Top             =   2040
      _ExtentX        =   741
      _ExtentY        =   741
      _Version        =   393216
   End
   Begin VB.CommandButton Command1 
      Caption         =   "�ǡ�¼"
      Height          =   495
      Left            =   1680
      TabIndex        =   4
      Top             =   2040
      Width           =   1215
   End
   Begin VB.TextBox Text2 
      Height          =   375
      IMEMode         =   3  'DISABLE
      Left            =   1800
      PasswordChar    =   "*"
      TabIndex        =   3
      Top             =   1080
      Width           =   1815
   End
   Begin VB.TextBox Text1 
      Height          =   375
      Left            =   1800
      TabIndex        =   2
      Top             =   600
      Width           =   1815
   End
   Begin VB.Label Label2 
      Caption         =   "�ܡ���:"
      Height          =   255
      Left            =   600
      TabIndex        =   1
      Top             =   1140
      Width           =   855
   End
   Begin VB.Label Label1 
      Caption         =   "�û���:"
      Height          =   255
      Left            =   600
      TabIndex        =   0
      Top             =   660
      Width           =   855
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
Private rsa As rsa

Dim sid As String
Dim e As Long
Dim n As Long
Dim tm As Boolean
Dim log As Boolean

Private Sub Command1_Click()
    Command1.Caption = "���ڵ�¼..."
    Command1.Enabled = False
    
    Dim login As String
    Dim xl As Variant
        
    If sockClient.State <> sckClosed Then
        sockClient.Close
    End If
    
    sockClient.RemoteHost = "127.0.0.1"
    sockClient.RemotePort = 1228
    sockClient.Protocol = sckTCPProtocol             '���� Winsock �ؼ���ʹ�õ�Э����TCP��
    sockClient.Connect                               'Ҫ�����ӵ�Զ�̼����
    'MsgBox "OK0"
    xl = GetTickCount()
    Do While sockClient.State <> sckConnected
        If ((GetTickCount() - xl) / 1000 > 2) Then
            Exit Do
        End If
        DoEvents
    Loop
    ' MsgBox "OK1"
    If sockClient.State = sckConnected Then
    '    MsgBox "OK2"
        sockClient.SendData "*"
        DoEvents
    Else
        MsgBox "����ʧ��", vbInformation, "��ʾ"
        Unload Me
    End If
        
    '�ȴ���ȡsid��key
    xl = GetTickCount()
    Do While log <> True
        If ((GetTickCount() - xl) / 1000 > 2) Then
            Exit Do
        End If
        DoEvents
    Loop
    
    If (log = False) Then
        MsgBox "����ʧ��", vbInformation, "��ʾ"
        Unload Me
    End If
    
    log = False
    
    xl = GetTickCount()
    login = Me.Text1.Text & "/HL.SP/" & Me.Text2.Text
    
    If sockClient.State <> sckClosed Then
        sockClient.Close
    End If
    
    sockClient.RemoteHost = "127.0.0.1"
    sockClient.RemotePort = 1228
    sockClient.Protocol = sckTCPProtocol             '���� Winsock �ؼ���ʹ�õ�Э����TCP��
    sockClient.Connect                               'Ҫ�����ӵ�Զ�̼����
        
    Do While sockClient.State <> sckConnected
        If ((GetTickCount() - xl) / 1000 > 2) Then
            Exit Do
        End If
        DoEvents
    Loop
 
    If sockClient.State = sckConnected Then
        sockClient.SendData sid & "/HL.SP/" & rsa.Encode("01/HL.SP/" & login, e, n)
        DoEvents
    Else
        MsgBox "����ʧ��", vbInformation, "��ʾ"
        Unload Me
    End If
    
End Sub

Private Sub Form_Load()
    Set rsa = New rsa
    tm = False
    log = False
    
End Sub

Private Sub Form_Unload(Cancel As Integer)
    Unload Form2
End Sub

Private Sub sockClient_DataArrival(ByVal bytesTotal As Long)
    Dim tmpData() As Byte
    Dim Finis As String, FinisData(3) As Byte   '��ȡ��β��־
    Static rsLength As Long
    Static iCount As Integer
    Static rsData() As Byte  '��¼������
    'Dim arrData() As Byte
    'Dim objRec As Recordset
    sockClient.GetData tmpData, vbArray + vbByte
    '�ѽ��յ����ݱ�����rsData��
    
        If iCount = 0 Then              '��һ�ν���
    
            rsData = tmpData
    
            iCount = 1
    
        Else
            ReDim Preserve rsData(rsLength + bytesTotal)
            CopyMemory rsData(rsLength + 1), tmpData(0), bytesTotal
        End If
        rsLength = UBound(rsData)    'rsData�ĳ���
        CopyMemory FinisData(0), rsData(rsLength - 3), 4
        Finis = FinisData
        If Finis = "`!" Then   '�����ǽ�β���ضϽ�β��־����
            ReDim Preserve tmpData(rsLength - 4)
            Dim content As String
            Dim tc As String
            Dim tcs() As String
            
            content = tmpData()
            
            If (InStr(content, "*/HL.SP/") <> 0) Then
                Debug.Print "<-" & content
                tcs() = Split(content, "/HL.SP/")
                
                If (tcs(1) = "1") Then
                    sid = tcs(2)
                    e = tcs(3)
                    n = tcs(4)
                    log = True
                 Else
                    MsgBox "�����SID�����޷�����SID", vbInformation, "��ʾ"
                    Unload Me
                 End If
            Else
                tc = rsa.Decode(content, e, n)
                Debug.Print "<-" & tc
                tcs() = Split(tc, "/HL.SP/")
                
                'Check tcs
'                If (tcs(0) = "00") Then
'                     If (tcs(1) = "1") Then
'                       sid = tcs(2)
'                        'tm = True
'                        'Debug.Print "sid=" & sid
'                     Else
'                        sid = ""
'                        'tm = False
'                     End If
'                Else
                If (tcs(0) = "01") Then 'login
                     If (tcs(1) = "1") Then
                        MsgBox "��¼�ɹ�", vbInformation, "��ʾ"
                        Command1.Caption = "�ǡ�¼"
                        Command1.Enabled = True
                        Form2.Show
                        Me.Hide
                        tm = True
                    ElseIf (tcs(1) = "0") Then
                        MsgBox "��¼ʧ��", vbInformation, "��ʾ"
                        Command1.Caption = "�ǡ�¼"
                        Command1.Enabled = True
                        tm = False
                    End If
                ElseIf (tcs(0) = "02") Then    'refresh
                    If (tcs(1) = "1") Then
                        tm = True
                    ElseIf (tcs(1) = "0") Then
                        MsgBox "�����жϣ������µ�¼", vbInformation, "����"
                        Form2.Hide
                        Me.Show
                        tm = False
                    ElseIf (tcs(1) = "A") Then
                        MsgBox "��ǰ�ʺ��������ط�����¼", vbInformation, "��ʾ"
                        Form2.Hide
                        Me.Show
                        tm = False
                    End If
                End If
            End If
            

            
            Erase rsData
    
            iCount = 0
    
            rsLength = 0
        End If
        
        sockClient.Close
        
End Sub

Private Sub Timer1_Timer()
    'Debug.Print "timer_client" & tm
    'send a refresh command
    Dim xl As Variant
    xl = GetTickCount
    
    If (tm) Then
        tm = False
        Dim content As String
        If (sid <> "") Then
            content = "02/HL.SP/*"
        Else
            MsgBox "�����жϣ������µ�¼", vbInformation, "����"
            tm = False
            Form2.Hide
            Me.Show
            Exit Sub
        End If
        
        If sockClient.State <> sckClosed Then sockClient.Close
        
        sockClient.RemoteHost = "127.0.0.1"
        sockClient.RemotePort = 1228
        sockClient.Protocol = sckTCPProtocol             '���� Winsock �ؼ���ʹ�õ�Э����TCP��
        sockClient.Connect                               'Ҫ�����ӵ�Զ�̼����
        
        Do While sockClient.State <> sckConnected
            If ((GetTickCount() - xl) / 1000 > 2) Then
                Exit Do
            End If
            DoEvents
        Loop
    
        If sockClient.State = sckConnected Then
            sockClient.SendData sid & "/HL.SP/" & rsa.Encode(content, e, n)
            DoEvents
        Else
            tm = False
            MsgBox "����ʧ��", vbInformation, "��ʾ"
            Unload Me
            'Form2.Hide
            'Me.Show
        End If
        'sockClient.Close
    End If
End Sub

