VERSION 1.0 CLASS
BEGIN
  MultiUse = -1  'True
  Persistable = 0  'NotPersistable
  DataBindingBehavior = 0  'vbNone
  DataSourceBehavior  = 0  'vbNone
  MTSTransactionMode  = 0  'NotAnMTSObject
END
Attribute VB_Name = "Util"
Attribute VB_GlobalNameSpace = False
Attribute VB_Creatable = True
Attribute VB_PredeclaredId = False
Attribute VB_Exposed = False
  
    Function genStr() As String
        Dim i As Integer
        Dim j As Integer
        Dim ch As Integer
            
        For j = 1 To 8
            i = Int(3 * Rnd + 1)
            Select Case i
                Case 1
                    ch = Int(26 * Rnd + 65)
                Case 2
                    ch = Int(26 * Rnd + 97)
                Case 3
                    ch = Int(10 * Rnd + 48)
            End Select
            genStr = genStr & Chr(ch)
        Next
    End Function
