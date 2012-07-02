VERSION 1.0 CLASS
BEGIN
  MultiUse = -1  'True
  Persistable = 0  'NotPersistable
  DataBindingBehavior = 0  'vbNone
  DataSourceBehavior  = 0  'vbNone
  MTSTransactionMode  = 0  'NotAnMTSObject
END
Attribute VB_Name = "RSA"
Attribute VB_GlobalNameSpace = False
Attribute VB_Creatable = True
Attribute VB_PredeclaredId = False
Attribute VB_Exposed = False
Private key(1 To 3) As Long
Private Const base64 = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/"

Public Function GetKey() As Variant
    Dim a(2) As String
    
    For i = 0 To 2
        a(i) = key(i + 1)
    Next
 
    GetKey = a
    
End Function

Public Sub GenKey()
Dim d As Long, phi As Long, e As Long
Dim m As Long, x As Long, q As Long
Dim p As Long
Randomize
On Error GoTo top
top:
p = Rnd * 1000 \ 1
If IsPrime(p) = False Then GoTo top
Sel_q:
q = Rnd * 1000 \ 1
If IsPrime(q) = False Then GoTo Sel_q
n = p * q \ 1
phi = (p - 1) * (q - 1) \ 1
d = Rnd * n \ 1
If d = 0 Or n = 0 Or d = 1 Then GoTo top
e = Euler(phi, d)
If e = 0 Or e = 1 Then GoTo top

x = Mult(255, e, n)
If Not Mult(x, d, n) = 255 Then
    DoEvents
    GoTo top
ElseIf Mult(x, d, n) = 255 Then
    key(1) = e
    key(2) = d
    key(3) = n
End If
End Sub

Private Function Euler(ByVal a As Long, ByVal b As Long) As Long
On Error GoTo error2
r1 = a: r = b
p1 = 0: p = 1
q1 = 2: q = 0
n = -1
Do Until r = 0
    r2 = r1: r1 = r
    p2 = p1: p1 = p
    q2 = q1: q1 = q
    n = n + 1
    r = r2 Mod r1
    c = r2 \ r1
    p = (c * p1) + p2
    q = (c * q1) + q2
Loop
s = (b * p1) - (a * q1)
If s > 0 Then
    x = p1
Else
    x = (0 - p1) + a
End If
Euler = x
Exit Function

error2:
Euler = 0
End Function

Private Function Mult(ByVal x As Long, ByVal p As Long, ByVal m As Long) As Long
y = 1
On Error GoTo error1
Do While p > 0
    Do While (p / 2) = (p \ 2)
        x = (x * x) Mod m
        p = p / 2
    Loop
    y = (x * y) Mod m
    p = p - 1
Loop
Mult = y
Exit Function

error1:
y = 0
End Function

Private Function IsPrime(lngNumber As Long) As Boolean
Dim lngCount As Long
Dim lngSqr As Long
Dim x As Long

    lngSqr = Sqr(lngNumber) ' get the int square root

    If lngNumber < 2 Then
IsPrime = False
Exit Function
End If

lngCount = 2
IsPrime = True

If lngNumber Mod lngCount = 0& Then
IsPrime = False
Exit Function
End If

lngCount = 3

For x& = lngCount To lngSqr Step 2
If lngNumber Mod x& = 0 Then
IsPrime = False
Exit Function
End If
Next
End Function

Private Function Base64_Encode(DecryptedText As String) As String
Dim c1, c2, c3 As Integer
Dim w1 As Integer
Dim w2 As Integer
Dim w3 As Integer
Dim w4 As Integer
Dim n As Integer
Dim retry As String
For n = 1 To Len(DecryptedText) Step 3
c1 = Asc(Mid$(DecryptedText, n, 1))
c2 = Asc(Mid$(DecryptedText, n + 1, 1) + Chr$(0))
c3 = Asc(Mid$(DecryptedText, n + 2, 1) + Chr$(0))
w1 = Int(c1 / 4)
w2 = (c1 And 3) * 16 + Int(c2 / 16)
If Len(DecryptedText) >= n + 1 Then w3 = (c2 And 15) * 4 + Int(c3 / 64) Else w3 = -1
      If Len(DecryptedText) >= n + 2 Then w4 = c3 And 63 Else w4 = -1

      retry = retry + mimeencode(w1) + mimeencode(w2) + mimeencode(w3) + mimeencode(w4)
   Next
   Base64_Encode = retry
End Function

Private Function Base64_Decode(a As String) As String
Dim w1 As Integer
Dim w2 As Integer
Dim w3 As Integer
Dim w4 As Integer
Dim n As Integer
Dim retry As String

   For n = 1 To Len(a) Step 4
      w1 = mimedecode(Mid$(a, n, 1))
      w2 = mimedecode(Mid$(a, n + 1, 1))
      w3 = mimedecode(Mid$(a, n + 2, 1))
      w4 = mimedecode(Mid$(a, n + 3, 1))
      If w2 >= 0 Then retry = retry + Chr$(((w1 * 4 + Int(w2 / 16)) And 255))
      If w3 >= 0 Then retry = retry + Chr$(((w2 * 16 + Int(w3 / 4)) And 255))
      If w4 >= 0 Then retry = retry + Chr$(((w3 * 64 + w4) And 255))
   Next
   Base64_Decode = retry
End Function

Private Function mimeencode(w As Integer) As String
   If w >= 0 Then mimeencode = Mid$(base64, w + 1, 1) Else mimeencode = ""
End Function

Private Function mimedecode(a As String) As Integer
   If Len(a) = 0 Then mimedecode = -1: Exit Function
   mimedecode = InStr(base64, a) - 1
End Function

Public Function Encode(ByVal Inp As String, ByVal e As Long, ByVal n As Long) As String
Dim s As String
s = ""
m = Inp

If m = "" Then Exit Function
s = Mult(CLng(Asc(Mid(m, 1, 1))), e, n)
For i = 2 To Len(m)
    s = s & "+" & Mult(CLng(Asc(Mid(m, i, 1))), e, n)
Next i
Encode = Base64_Encode(s)
End Function

Public Function Decode(ByVal Inp As String, ByVal d As Long, ByVal n As Long) As String
St = ""
ind = Base64_Decode(Inp)
For i = 1 To Len(ind)
    nxt = InStr(i, ind, "+")
    If Not nxt = 0 Then
        tok = Val(Mid(ind, i, nxt))
    Else
        tok = Val(Mid(ind, i))
    End If
    St = St + Chr(Mult(CLng(tok), d, n))
    If Not nxt = 0 Then
        i = nxt
    Else
        i = Len(ind)
    End If
Next i
Decode = St
End Function
