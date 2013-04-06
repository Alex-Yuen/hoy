package ws.hoyland.cc.random;

public final class e extends a
{
  private int a;
  private int b;
  private int c;
  private int d;
  private int e;
  private int[] f;
  private int g;

  public e()
  {
    int[] arrayOfInt = new int[80];
    this.f = arrayOfInt;
    b();
  }

  private static void a(int paramInt1, byte[] paramArrayOfByte, int paramInt2)
  {
    int i = paramInt2 + 1;
    byte j = (byte)(paramInt1 >>> 24);
    paramArrayOfByte[paramInt2] = j;
    int k = i + 1;
    byte m = (byte)(paramInt1 >>> 16);
    paramArrayOfByte[i] = m;
    int n = k + 1;
    byte i1 = (byte)(paramInt1 >>> 8);
    paramArrayOfByte[k] = i1;
    byte i2 = (byte)paramInt1;
    paramArrayOfByte[n] = i2;
  }

  public final int a(byte[] paramArrayOfByte)
  {
    a();
    a(this.a, paramArrayOfByte, 0);
    a(this.b, paramArrayOfByte, 4);
    a(this.c, paramArrayOfByte, 8);
    a(this.d, paramArrayOfByte, 12);
    a(this.e, paramArrayOfByte, 16);
    b();
    return 20;
  }

  protected final void a(long paramLong)
  {
    if (this.g > 14)
      c();
    int[] arrayOfInt1 = this.f;
    int i = (int)(paramLong >>> 32);
    arrayOfInt1[14] = i;
    int[] arrayOfInt2 = this.f;
    int j = (int)(0xFFFF & paramLong);
    arrayOfInt2[15] = j;
  }

  protected final void a(byte[] paramArrayOfByte, int paramInt)
  {
    int[] arrayOfInt = this.f;
    int i = this.g;
    int j = i + 1;
    this.g = j;
    int k = (paramArrayOfByte[paramInt] & 0xFF) << 24;
    int m = paramInt + 1;
    int n = (paramArrayOfByte[m] & 0xFF) << 16;
    int i1 = k | n;
    int i2 = paramInt + 2;
    int i3 = (paramArrayOfByte[i2] & 0xFF) << 8;
    int i4 = i1 | i3;
    int i5 = paramInt + 3;
    int i6 = paramArrayOfByte[i5] & 0xFF;
    int i7 = i4 | i6;
    arrayOfInt[i] = i7;
    if (this.g == 16)
      c();
  }

  public final void b()
  {
    super.b();
    this.a = 1732584193;
    this.b = -271733879;
    this.c = -1732584194;
    this.d = 271733878;
    this.e = -1009589776;
    this.g = 0;
    int i = 0;
    while (true)
    {
      int j = this.f.length;
      if (i == j)
        break;
      this.f[i] = 0;
      i += 1;
    }
  }

  // ERROR //
  protected final void c()
  {
    // Byte code:
    //   0: bipush 16
    //   2: istore_1
    //   3: iload_1
    //   4: bipush 80
    //   6: if_icmpge +130 -> 136
    //   9: aload_0
    //   10: getfield 20	com/tencent/token/core/encrypt/random/e:f	[I
    //   13: astore_2
    //   14: iload_1
    //   15: iconst_3
    //   16: isub
    //   17: istore_3
    //   18: aload_2
    //   19: iload_3
    //   20: iaload
    //   21: istore 4
    //   23: aload_0
    //   24: getfield 20	com/tencent/token/core/encrypt/random/e:f	[I
    //   27: astore 5
    //   29: iload_1
    //   30: bipush 8
    //   32: isub
    //   33: istore 6
    //   35: aload 5
    //   37: iload 6
    //   39: iaload
    //   40: istore 7
    //   42: iload 4
    //   44: iload 7
    //   46: ixor
    //   47: istore 8
    //   49: aload_0
    //   50: getfield 20	com/tencent/token/core/encrypt/random/e:f	[I
    //   53: astore 9
    //   55: iload_1
    //   56: bipush 14
    //   58: isub
    //   59: istore 10
    //   61: aload 9
    //   63: iload 10
    //   65: iaload
    //   66: istore 11
    //   68: iload 8
    //   70: iload 11
    //   72: ixor
    //   73: istore 12
    //   75: aload_0
    //   76: getfield 20	com/tencent/token/core/encrypt/random/e:f	[I
    //   79: astore 13
    //   81: iload_1
    //   82: bipush 16
    //   84: isub
    //   85: istore 14
    //   87: aload 13
    //   89: iload 14
    //   91: iaload
    //   92: istore 15
    //   94: iload 12
    //   96: iload 15
    //   98: ixor
    //   99: istore 16
    //   101: aload_0
    //   102: getfield 20	com/tencent/token/core/encrypt/random/e:f	[I
    //   105: astore 17
    //   107: iload 16
    //   109: iconst_1
    //   110: ishl
    //   111: istore 18
    //   113: iload 16
    //   115: bipush 31
    //   117: iushr
    //   118: iload 18
    //   120: ior
    //   121: istore 19
    //   123: aload 17
    //   125: iload_1
    //   126: iload 19
    //   128: iastore
    //   129: iload_1
    //   130: iconst_1
    //   131: iadd
    //   132: istore_1
    //   133: goto -130 -> 3
    //   136: aload_0
    //   137: getfield 28	com/tencent/token/core/encrypt/random/e:a	I
    //   140: istore 20
    //   142: aload_0
    //   143: getfield 32	com/tencent/token/core/encrypt/random/e:b	I
    //   146: istore 21
    //   148: aload_0
    //   149: getfield 34	com/tencent/token/core/encrypt/random/e:c	I
    //   152: istore 22
    //   154: aload_0
    //   155: getfield 36	com/tencent/token/core/encrypt/random/e:d	I
    //   158: istore 23
    //   160: aload_0
    //   161: getfield 38	com/tencent/token/core/encrypt/random/e:e	I
    //   164: istore 24
    //   166: iconst_0
    //   167: istore 25
    //   169: iload 20
    //   171: istore 26
    //   173: iload 25
    //   175: istore_1
    //   176: iload 24
    //   178: istore 27
    //   180: iload 22
    //   182: istore 28
    //   184: iload 27
    //   186: istore 29
    //   188: iload 21
    //   190: istore 30
    //   192: iconst_0
    //   193: istore 31
    //   195: iload 30
    //   197: istore 32
    //   199: iload_1
    //   200: iconst_4
    //   201: if_icmpge +541 -> 742
    //   204: iload 26
    //   206: iconst_5
    //   207: ishl
    //   208: istore 33
    //   210: iload 26
    //   212: bipush 27
    //   214: iushr
    //   215: istore 34
    //   217: iload 33
    //   219: iload 34
    //   221: ior
    //   222: istore 35
    //   224: iload 32
    //   226: iload 28
    //   228: iand
    //   229: istore 36
    //   231: iload 32
    //   233: bipush 255
    //   235: ixor
    //   236: iload 23
    //   238: iand
    //   239: istore 37
    //   241: iload 36
    //   243: iload 37
    //   245: ior
    //   246: istore 38
    //   248: iload 35
    //   250: iload 38
    //   252: iadd
    //   253: istore 39
    //   255: aload_0
    //   256: getfield 20	com/tencent/token/core/encrypt/random/e:f	[I
    //   259: astore 40
    //   261: iload 31
    //   263: iconst_1
    //   264: iadd
    //   265: istore 41
    //   267: aload 40
    //   269: iload 31
    //   271: iaload
    //   272: iload 39
    //   274: iadd
    //   275: ldc 53
    //   277: iadd
    //   278: iload 29
    //   280: iadd
    //   281: istore 42
    //   283: iload 32
    //   285: bipush 30
    //   287: ishl
    //   288: istore 43
    //   290: iload 32
    //   292: iconst_2
    //   293: iushr
    //   294: istore 44
    //   296: iload 43
    //   298: iload 44
    //   300: ior
    //   301: istore 45
    //   303: iload 42
    //   305: iconst_5
    //   306: ishl
    //   307: istore 46
    //   309: iload 42
    //   311: bipush 27
    //   313: iushr
    //   314: istore 47
    //   316: iload 46
    //   318: iload 47
    //   320: ior
    //   321: istore 48
    //   323: iload 26
    //   325: iload 45
    //   327: iand
    //   328: istore 49
    //   330: iload 26
    //   332: bipush 255
    //   334: ixor
    //   335: iload 28
    //   337: iand
    //   338: istore 50
    //   340: iload 49
    //   342: iload 50
    //   344: ior
    //   345: istore 51
    //   347: iload 48
    //   349: iload 51
    //   351: iadd
    //   352: istore 52
    //   354: aload_0
    //   355: getfield 20	com/tencent/token/core/encrypt/random/e:f	[I
    //   358: astore 53
    //   360: iload 41
    //   362: iconst_1
    //   363: iadd
    //   364: istore 54
    //   366: aload 53
    //   368: iload 41
    //   370: iaload
    //   371: istore 55
    //   373: iload 52
    //   375: iload 55
    //   377: iadd
    //   378: ldc 53
    //   380: iadd
    //   381: istore 56
    //   383: iload 23
    //   385: iload 56
    //   387: iadd
    //   388: istore 57
    //   390: iload 26
    //   392: bipush 30
    //   394: ishl
    //   395: istore 58
    //   397: iload 26
    //   399: iconst_2
    //   400: iushr
    //   401: istore 59
    //   403: iload 58
    //   405: iload 59
    //   407: ior
    //   408: istore 60
    //   410: iload 57
    //   412: iconst_5
    //   413: ishl
    //   414: istore 61
    //   416: iload 57
    //   418: bipush 27
    //   420: iushr
    //   421: istore 62
    //   423: iload 61
    //   425: iload 62
    //   427: ior
    //   428: istore 63
    //   430: iload 42
    //   432: iload 60
    //   434: iand
    //   435: istore 64
    //   437: iload 42
    //   439: bipush 255
    //   441: ixor
    //   442: iload 45
    //   444: iand
    //   445: istore 65
    //   447: iload 64
    //   449: iload 65
    //   451: ior
    //   452: istore 66
    //   454: iload 63
    //   456: iload 66
    //   458: iadd
    //   459: istore 67
    //   461: aload_0
    //   462: getfield 20	com/tencent/token/core/encrypt/random/e:f	[I
    //   465: astore 68
    //   467: iload 54
    //   469: iconst_1
    //   470: iadd
    //   471: istore 69
    //   473: aload 68
    //   475: iload 54
    //   477: iaload
    //   478: istore 70
    //   480: iload 67
    //   482: iload 70
    //   484: iadd
    //   485: ldc 53
    //   487: iadd
    //   488: istore 71
    //   490: iload 28
    //   492: iload 71
    //   494: iadd
    //   495: istore 72
    //   497: iload 42
    //   499: bipush 30
    //   501: ishl
    //   502: istore 73
    //   504: iload 42
    //   506: iconst_2
    //   507: iushr
    //   508: iload 73
    //   510: ior
    //   511: istore 74
    //   513: iload 72
    //   515: iconst_5
    //   516: ishl
    //   517: istore 75
    //   519: iload 72
    //   521: bipush 27
    //   523: iushr
    //   524: istore 76
    //   526: iload 75
    //   528: iload 76
    //   530: ior
    //   531: istore 77
    //   533: iload 57
    //   535: iload 74
    //   537: iand
    //   538: istore 78
    //   540: iload 57
    //   542: bipush 255
    //   544: ixor
    //   545: iload 60
    //   547: iand
    //   548: istore 79
    //   550: iload 78
    //   552: iload 79
    //   554: ior
    //   555: istore 80
    //   557: iload 77
    //   559: iload 80
    //   561: iadd
    //   562: istore 81
    //   564: aload_0
    //   565: getfield 20	com/tencent/token/core/encrypt/random/e:f	[I
    //   568: astore 82
    //   570: iload 69
    //   572: iconst_1
    //   573: iadd
    //   574: istore 83
    //   576: aload 82
    //   578: iload 69
    //   580: iaload
    //   581: istore 84
    //   583: iload 81
    //   585: iload 84
    //   587: iadd
    //   588: ldc 53
    //   590: iadd
    //   591: istore 85
    //   593: iload 45
    //   595: iload 85
    //   597: iadd
    //   598: istore 86
    //   600: iload 57
    //   602: bipush 30
    //   604: ishl
    //   605: istore 87
    //   607: iload 57
    //   609: iconst_2
    //   610: iushr
    //   611: iload 87
    //   613: ior
    //   614: istore 23
    //   616: iload 86
    //   618: iconst_5
    //   619: ishl
    //   620: istore 88
    //   622: iload 86
    //   624: bipush 27
    //   626: iushr
    //   627: istore 89
    //   629: iload 88
    //   631: iload 89
    //   633: ior
    //   634: istore 90
    //   636: iload 72
    //   638: iload 23
    //   640: iand
    //   641: istore 91
    //   643: iload 72
    //   645: bipush 255
    //   647: ixor
    //   648: iload 74
    //   650: iand
    //   651: istore 92
    //   653: iload 91
    //   655: iload 92
    //   657: ior
    //   658: istore 93
    //   660: iload 90
    //   662: iload 93
    //   664: iadd
    //   665: istore 94
    //   667: aload_0
    //   668: getfield 20	com/tencent/token/core/encrypt/random/e:f	[I
    //   671: astore 95
    //   673: iload 83
    //   675: iconst_1
    //   676: iadd
    //   677: istore 96
    //   679: aload 95
    //   681: iload 83
    //   683: iaload
    //   684: istore 97
    //   686: iload 94
    //   688: iload 97
    //   690: iadd
    //   691: ldc 53
    //   693: iadd
    //   694: istore 98
    //   696: iload 60
    //   698: iload 98
    //   700: iadd
    //   701: istore 99
    //   703: iload 72
    //   705: bipush 30
    //   707: ishl
    //   708: istore 100
    //   710: iload 72
    //   712: iconst_2
    //   713: iushr
    //   714: iload 100
    //   716: ior
    //   717: istore 28
    //   719: iload_1
    //   720: iconst_1
    //   721: iadd
    //   722: istore_1
    //   723: iload 99
    //   725: istore 26
    //   727: iload 86
    //   729: istore 32
    //   731: iload 74
    //   733: istore 29
    //   735: iload 96
    //   737: istore 31
    //   739: goto -540 -> 199
    //   742: iconst_0
    //   743: istore_1
    //   744: iload_1
    //   745: iconst_4
    //   746: if_icmpge +471 -> 1217
    //   749: iload 26
    //   751: iconst_5
    //   752: ishl
    //   753: istore 101
    //   755: iload 26
    //   757: bipush 27
    //   759: iushr
    //   760: istore 102
    //   762: iload 101
    //   764: iload 102
    //   766: ior
    //   767: istore 103
    //   769: iload 32
    //   771: iload 28
    //   773: ixor
    //   774: iload 23
    //   776: ixor
    //   777: istore 104
    //   779: iload 103
    //   781: iload 104
    //   783: iadd
    //   784: istore 105
    //   786: aload_0
    //   787: getfield 20	com/tencent/token/core/encrypt/random/e:f	[I
    //   790: astore 106
    //   792: iload 31
    //   794: iconst_1
    //   795: iadd
    //   796: istore 107
    //   798: aload 106
    //   800: iload 31
    //   802: iaload
    //   803: iload 105
    //   805: iadd
    //   806: ldc 54
    //   808: iadd
    //   809: iload 29
    //   811: iadd
    //   812: istore 108
    //   814: iload 32
    //   816: bipush 30
    //   818: ishl
    //   819: istore 109
    //   821: iload 32
    //   823: iconst_2
    //   824: iushr
    //   825: istore 110
    //   827: iload 109
    //   829: iload 110
    //   831: ior
    //   832: istore 111
    //   834: iload 108
    //   836: iconst_5
    //   837: ishl
    //   838: istore 112
    //   840: iload 108
    //   842: bipush 27
    //   844: iushr
    //   845: istore 113
    //   847: iload 112
    //   849: iload 113
    //   851: ior
    //   852: istore 114
    //   854: iload 26
    //   856: iload 111
    //   858: ixor
    //   859: iload 28
    //   861: ixor
    //   862: istore 115
    //   864: iload 114
    //   866: iload 115
    //   868: iadd
    //   869: istore 116
    //   871: aload_0
    //   872: getfield 20	com/tencent/token/core/encrypt/random/e:f	[I
    //   875: astore 117
    //   877: iload 107
    //   879: iconst_1
    //   880: iadd
    //   881: istore 118
    //   883: aload 117
    //   885: iload 107
    //   887: iaload
    //   888: istore 119
    //   890: iload 116
    //   892: iload 119
    //   894: iadd
    //   895: ldc 54
    //   897: iadd
    //   898: istore 120
    //   900: iload 23
    //   902: iload 120
    //   904: iadd
    //   905: istore 121
    //   907: iload 26
    //   909: bipush 30
    //   911: ishl
    //   912: istore 122
    //   914: iload 26
    //   916: iconst_2
    //   917: iushr
    //   918: istore 123
    //   920: iload 122
    //   922: iload 123
    //   924: ior
    //   925: istore 124
    //   927: iload 121
    //   929: iconst_5
    //   930: ishl
    //   931: istore 125
    //   933: iload 121
    //   935: bipush 27
    //   937: iushr
    //   938: istore 126
    //   940: iload 125
    //   942: iload 126
    //   944: ior
    //   945: istore 127
    //   947: iload 108
    //   949: iload 124
    //   951: ixor
    //   952: iload 111
    //   954: ixor
    //   955: istore 128
    //   957: iload 127
    //   959: iload 128
    //   961: iadd
    //   962: istore 129
    //   964: aload_0
    //   965: getfield 20	com/tencent/token/core/encrypt/random/e:f	[I
    //   968: astore 130
    //   970: iload 118
    //   972: iconst_1
    //   973: iadd
    //   974: istore 131
    //   976: aload 130
    //   978: iload 118
    //   980: iaload
    //   981: istore 132
    //   983: iload 129
    //   985: iload 132
    //   987: iadd
    //   988: ldc 54
    //   990: iadd
    //   991: istore 133
    //   993: iload 28
    //   995: iload 133
    //   997: iadd
    //   998: istore 134
    //   1000: iload 108
    //   1002: bipush 30
    //   1004: ishl
    //   1005: istore 135
    //   1007: iload 108
    //   1009: iconst_2
    //   1010: iushr
    //   1011: iload 135
    //   1013: ior
    //   1014: istore 136
    //   1016: iload 134
    //   1018: iconst_5
    //   1019: ishl
    //   1020: istore 137
    //   1022: iload 134
    //   1024: bipush 27
    //   1026: iushr
    //   1027: istore 138
    //   1029: iload 137
    //   1031: iload 138
    //   1033: ior
    //   1034: istore 139
    //   1036: iload 121
    //   1038: iload 136
    //   1040: ixor
    //   1041: iload 124
    //   1043: ixor
    //   1044: istore 140
    //   1046: iload 139
    //   1048: iload 140
    //   1050: iadd
    //   1051: istore 141
    //   1053: aload_0
    //   1054: getfield 20	com/tencent/token/core/encrypt/random/e:f	[I
    //   1057: astore 142
    //   1059: iload 131
    //   1061: iconst_1
    //   1062: iadd
    //   1063: istore 143
    //   1065: aload 142
    //   1067: iload 131
    //   1069: iaload
    //   1070: istore 144
    //   1072: iload 141
    //   1074: iload 144
    //   1076: iadd
    //   1077: ldc 54
    //   1079: iadd
    //   1080: istore 145
    //   1082: iload 111
    //   1084: iload 145
    //   1086: iadd
    //   1087: istore 146
    //   1089: iload 121
    //   1091: bipush 30
    //   1093: ishl
    //   1094: istore 147
    //   1096: iload 121
    //   1098: iconst_2
    //   1099: iushr
    //   1100: iload 147
    //   1102: ior
    //   1103: istore 23
    //   1105: iload 146
    //   1107: iconst_5
    //   1108: ishl
    //   1109: istore 148
    //   1111: iload 146
    //   1113: bipush 27
    //   1115: iushr
    //   1116: istore 149
    //   1118: iload 148
    //   1120: iload 149
    //   1122: ior
    //   1123: istore 150
    //   1125: iload 134
    //   1127: iload 23
    //   1129: ixor
    //   1130: iload 136
    //   1132: ixor
    //   1133: istore 151
    //   1135: iload 150
    //   1137: iload 151
    //   1139: iadd
    //   1140: istore 152
    //   1142: aload_0
    //   1143: getfield 20	com/tencent/token/core/encrypt/random/e:f	[I
    //   1146: astore 153
    //   1148: iload 143
    //   1150: iconst_1
    //   1151: iadd
    //   1152: istore 154
    //   1154: aload 153
    //   1156: iload 143
    //   1158: iaload
    //   1159: istore 155
    //   1161: iload 152
    //   1163: iload 155
    //   1165: iadd
    //   1166: ldc 54
    //   1168: iadd
    //   1169: istore 156
    //   1171: iload 124
    //   1173: iload 156
    //   1175: iadd
    //   1176: istore 157
    //   1178: iload 134
    //   1180: bipush 30
    //   1182: ishl
    //   1183: istore 158
    //   1185: iload 134
    //   1187: iconst_2
    //   1188: iushr
    //   1189: iload 158
    //   1191: ior
    //   1192: istore 28
    //   1194: iload_1
    //   1195: iconst_1
    //   1196: iadd
    //   1197: istore_1
    //   1198: iload 157
    //   1200: istore 26
    //   1202: iload 146
    //   1204: istore 32
    //   1206: iload 136
    //   1208: istore 29
    //   1210: iload 154
    //   1212: istore 31
    //   1214: goto -470 -> 744
    //   1217: iconst_0
    //   1218: istore_1
    //   1219: iload_1
    //   1220: iconst_4
    //   1221: if_icmpge +596 -> 1817
    //   1224: iload 26
    //   1226: iconst_5
    //   1227: ishl
    //   1228: istore 159
    //   1230: iload 26
    //   1232: bipush 27
    //   1234: iushr
    //   1235: istore 160
    //   1237: iload 159
    //   1239: iload 160
    //   1241: ior
    //   1242: istore 161
    //   1244: iload 32
    //   1246: iload 28
    //   1248: iand
    //   1249: istore 162
    //   1251: iload 32
    //   1253: iload 23
    //   1255: iand
    //   1256: istore 163
    //   1258: iload 162
    //   1260: iload 163
    //   1262: ior
    //   1263: istore 164
    //   1265: iload 28
    //   1267: iload 23
    //   1269: iand
    //   1270: istore 165
    //   1272: iload 164
    //   1274: iload 165
    //   1276: ior
    //   1277: istore 166
    //   1279: iload 161
    //   1281: iload 166
    //   1283: iadd
    //   1284: istore 167
    //   1286: aload_0
    //   1287: getfield 20	com/tencent/token/core/encrypt/random/e:f	[I
    //   1290: astore 168
    //   1292: iload 31
    //   1294: iconst_1
    //   1295: iadd
    //   1296: istore 169
    //   1298: aload 168
    //   1300: iload 31
    //   1302: iaload
    //   1303: iload 167
    //   1305: iadd
    //   1306: ldc 55
    //   1308: iadd
    //   1309: iload 29
    //   1311: iadd
    //   1312: istore 170
    //   1314: iload 32
    //   1316: bipush 30
    //   1318: ishl
    //   1319: istore 171
    //   1321: iload 32
    //   1323: iconst_2
    //   1324: iushr
    //   1325: istore 172
    //   1327: iload 171
    //   1329: iload 172
    //   1331: ior
    //   1332: istore 173
    //   1334: iload 170
    //   1336: iconst_5
    //   1337: ishl
    //   1338: istore 174
    //   1340: iload 170
    //   1342: bipush 27
    //   1344: iushr
    //   1345: istore 175
    //   1347: iload 174
    //   1349: iload 175
    //   1351: ior
    //   1352: istore 176
    //   1354: iload 26
    //   1356: iload 173
    //   1358: iand
    //   1359: istore 177
    //   1361: iload 26
    //   1363: iload 28
    //   1365: iand
    //   1366: istore 178
    //   1368: iload 177
    //   1370: iload 178
    //   1372: ior
    //   1373: istore 179
    //   1375: iload 173
    //   1377: iload 28
    //   1379: iand
    //   1380: istore 180
    //   1382: iload 179
    //   1384: iload 180
    //   1386: ior
    //   1387: istore 181
    //   1389: iload 176
    //   1391: iload 181
    //   1393: iadd
    //   1394: istore 182
    //   1396: aload_0
    //   1397: getfield 20	com/tencent/token/core/encrypt/random/e:f	[I
    //   1400: astore 183
    //   1402: iload 169
    //   1404: iconst_1
    //   1405: iadd
    //   1406: istore 184
    //   1408: aload 183
    //   1410: iload 169
    //   1412: iaload
    //   1413: istore 185
    //   1415: iload 182
    //   1417: iload 185
    //   1419: iadd
    //   1420: ldc 55
    //   1422: iadd
    //   1423: istore 186
    //   1425: iload 23
    //   1427: iload 186
    //   1429: iadd
    //   1430: istore 187
    //   1432: iload 26
    //   1434: bipush 30
    //   1436: ishl
    //   1437: istore 188
    //   1439: iload 26
    //   1441: iconst_2
    //   1442: iushr
    //   1443: istore 189
    //   1445: iload 188
    //   1447: iload 189
    //   1449: ior
    //   1450: istore 190
    //   1452: iload 187
    //   1454: iconst_5
    //   1455: ishl
    //   1456: istore 191
    //   1458: iload 187
    //   1460: bipush 27
    //   1462: iushr
    //   1463: istore 192
    //   1465: iload 191
    //   1467: iload 192
    //   1469: ior
    //   1470: istore 193
    //   1472: iload 170
    //   1474: iload 190
    //   1476: iand
    //   1477: istore 194
    //   1479: iload 170
    //   1481: iload 173
    //   1483: iand
    //   1484: istore 195
    //   1486: iload 194
    //   1488: iload 195
    //   1490: ior
    //   1491: istore 196
    //   1493: iload 190
    //   1495: iload 173
    //   1497: iand
    //   1498: istore 197
    //   1500: iload 196
    //   1502: iload 197
    //   1504: ior
    //   1505: istore 198
    //   1507: iload 193
    //   1509: iload 198
    //   1511: iadd
    //   1512: istore 199
    //   1514: aload_0
    //   1515: getfield 20	com/tencent/token/core/encrypt/random/e:f	[I
    //   1518: astore 200
    //   1520: iload 184
    //   1522: iconst_1
    //   1523: iadd
    //   1524: istore 201
    //   1526: aload 200
    //   1528: iload 184
    //   1530: iaload
    //   1531: istore 202
    //   1533: iload 199
    //   1535: iload 202
    //   1537: iadd
    //   1538: ldc 55
    //   1540: iadd
    //   1541: istore 203
    //   1543: iload 28
    //   1545: iload 203
    //   1547: iadd
    //   1548: istore 204
    //   1550: iload 170
    //   1552: bipush 30
    //   1554: ishl
    //   1555: istore 205
    //   1557: iload 170
    //   1559: iconst_2
    //   1560: iushr
    //   1561: iload 205
    //   1563: ior
    //   1564: istore 206
    //   1566: iload 204
    //   1568: iconst_5
    //   1569: ishl
    //   1570: istore 207
    //   1572: iload 204
    //   1574: bipush 27
    //   1576: iushr
    //   1577: istore 208
    //   1579: iload 207
    //   1581: iload 208
    //   1583: ior
    //   1584: istore 209
    //   1586: iload 187
    //   1588: iload 206
    //   1590: iand
    //   1591: istore 210
    //   1593: iload 187
    //   1595: iload 190
    //   1597: iand
    //   1598: istore 211
    //   1600: iload 210
    //   1602: iload 211
    //   1604: ior
    //   1605: istore 212
    //   1607: iload 206
    //   1609: iload 190
    //   1611: iand
    //   1612: istore 213
    //   1614: iload 212
    //   1616: iload 213
    //   1618: ior
    //   1619: istore 214
    //   1621: iload 209
    //   1623: iload 214
    //   1625: iadd
    //   1626: istore 215
    //   1628: aload_0
    //   1629: getfield 20	com/tencent/token/core/encrypt/random/e:f	[I
    //   1632: astore 216
    //   1634: iload 201
    //   1636: iconst_1
    //   1637: iadd
    //   1638: istore 217
    //   1640: aload 216
    //   1642: iload 201
    //   1644: iaload
    //   1645: istore 218
    //   1647: iload 215
    //   1649: iload 218
    //   1651: iadd
    //   1652: ldc 55
    //   1654: iadd
    //   1655: istore 219
    //   1657: iload 173
    //   1659: iload 219
    //   1661: iadd
    //   1662: istore 220
    //   1664: iload 187
    //   1666: bipush 30
    //   1668: ishl
    //   1669: istore 221
    //   1671: iload 187
    //   1673: iconst_2
    //   1674: iushr
    //   1675: iload 221
    //   1677: ior
    //   1678: istore 23
    //   1680: iload 220
    //   1682: iconst_5
    //   1683: ishl
    //   1684: istore 222
    //   1686: iload 220
    //   1688: bipush 27
    //   1690: iushr
    //   1691: istore 223
    //   1693: iload 222
    //   1695: iload 223
    //   1697: ior
    //   1698: istore 224
    //   1700: iload 204
    //   1702: iload 23
    //   1704: iand
    //   1705: istore 225
    //   1707: iload 204
    //   1709: iload 206
    //   1711: iand
    //   1712: istore 226
    //   1714: iload 225
    //   1716: iload 226
    //   1718: ior
    //   1719: istore 227
    //   1721: iload 23
    //   1723: iload 206
    //   1725: iand
    //   1726: istore 228
    //   1728: iload 227
    //   1730: iload 228
    //   1732: ior
    //   1733: istore 229
    //   1735: iload 224
    //   1737: iload 229
    //   1739: iadd
    //   1740: istore 230
    //   1742: aload_0
    //   1743: getfield 20	com/tencent/token/core/encrypt/random/e:f	[I
    //   1746: astore 231
    //   1748: iload 217
    //   1750: iconst_1
    //   1751: iadd
    //   1752: istore 232
    //   1754: aload 231
    //   1756: iload 217
    //   1758: iaload
    //   1759: istore 233
    //   1761: iload 230
    //   1763: iload 233
    //   1765: iadd
    //   1766: ldc 55
    //   1768: iadd
    //   1769: istore 234
    //   1771: iload 190
    //   1773: iload 234
    //   1775: iadd
    //   1776: istore 235
    //   1778: iload 204
    //   1780: bipush 30
    //   1782: ishl
    //   1783: istore 236
    //   1785: iload 204
    //   1787: iconst_2
    //   1788: iushr
    //   1789: iload 236
    //   1791: ior
    //   1792: istore 28
    //   1794: iload_1
    //   1795: iconst_1
    //   1796: iadd
    //   1797: istore_1
    //   1798: iload 235
    //   1800: istore 26
    //   1802: iload 220
    //   1804: istore 32
    //   1806: iload 206
    //   1808: istore 29
    //   1810: iload 232
    //   1812: istore 31
    //   1814: goto -595 -> 1219
    //   1817: iconst_0
    //   1818: istore_1
    //   1819: iload_1
    //   1820: iconst_3
    //   1821: if_icmpgt +659 -> 2480
    //   1824: iload 26
    //   1826: iconst_5
    //   1827: ishl
    //   1828: istore 237
    //   1830: iload 26
    //   1832: bipush 27
    //   1834: iushr
    //   1835: istore 238
    //   1837: iload 237
    //   1839: iload 238
    //   1841: ior
    //   1842: istore 239
    //   1844: iload 32
    //   1846: iload 28
    //   1848: ixor
    //   1849: iload 23
    //   1851: ixor
    //   1852: istore 240
    //   1854: iload 239
    //   1856: iload 240
    //   1858: iadd
    //   1859: istore 241
    //   1861: aload_0
    //   1862: getfield 20	com/tencent/token/core/encrypt/random/e:f	[I
    //   1865: astore 242
    //   1867: iload 31
    //   1869: iconst_1
    //   1870: iadd
    //   1871: istore 243
    //   1873: aload 242
    //   1875: iload 31
    //   1877: iaload
    //   1878: iload 241
    //   1880: iadd
    //   1881: ldc 56
    //   1883: iadd
    //   1884: iload 29
    //   1886: iadd
    //   1887: istore 244
    //   1889: iload 32
    //   1891: bipush 30
    //   1893: ishl
    //   1894: istore 245
    //   1896: iload 32
    //   1898: iconst_2
    //   1899: iushr
    //   1900: istore 246
    //   1902: iload 245
    //   1904: iload 246
    //   1906: ior
    //   1907: istore 247
    //   1909: iload 244
    //   1911: iconst_5
    //   1912: ishl
    //   1913: istore 248
    //   1915: iload 244
    //   1917: bipush 27
    //   1919: iushr
    //   1920: istore 249
    //   1922: iload 248
    //   1924: iload 249
    //   1926: ior
    //   1927: istore 250
    //   1929: iload 26
    //   1931: iload 247
    //   1933: ixor
    //   1934: iload 28
    //   1936: ixor
    //   1937: istore 251
    //   1939: iload 250
    //   1941: iload 251
    //   1943: iadd
    //   1944: istore 252
    //   1946: aload_0
    //   1947: getfield 20	com/tencent/token/core/encrypt/random/e:f	[I
    //   1950: astore 253
    //   1952: iload 243
    //   1954: iconst_1
    //   1955: iadd
    //   1956: istore 254
    //   1958: aload 253
    //   1960: iload 243
    //   1962: iaload
    //   1963: istore 255
    //   1965: iload 252
    //   1967: iload 255
    //   1969: iadd
    //   1970: ldc 56
    //   1972: iadd
    //   1973: wide
    //   1977: iload 23
    //   1979: wide
    //   1983: iadd
    //   1984: wide
    //   1988: iload 26
    //   1990: bipush 30
    //   1992: ishl
    //   1993: wide
    //   1997: iload 26
    //   1999: iconst_2
    //   2000: iushr
    //   2001: wide
    //   2005: wide
    //   2009: wide
    //   2013: ior
    //   2014: wide
    //   2018: wide
    //   2022: iconst_5
    //   2023: ishl
    //   2024: wide
    //   2028: wide
    //   2032: bipush 27
    //   2034: iushr
    //   2035: wide
    //   2039: wide
    //   2043: wide
    //   2047: ior
    //   2048: wide
    //   2052: iload 244
    //   2054: wide
    //   2058: ixor
    //   2059: iload 247
    //   2061: ixor
    //   2062: wide
    //   2066: wide
    //   2070: wide
    //   2074: iadd
    //   2075: wide
    //   2079: aload_0
    //   2080: getfield 20	com/tencent/token/core/encrypt/random/e:f	[I
    //   2083: wide
    //   2087: iload 254
    //   2089: iconst_1
    //   2090: iadd
    //   2091: wide
    //   2095: wide
    //   2099: iload 254
    //   2101: iaload
    //   2102: wide
    //   2106: wide
    //   2110: wide
    //   2114: iadd
    //   2115: ldc 56
    //   2117: iadd
    //   2118: wide
    //   2122: iload 28
    //   2124: wide
    //   2128: iadd
    //   2129: wide
    //   2133: iload 244
    //   2135: bipush 30
    //   2137: ishl
    //   2138: wide
    //   2142: iload 244
    //   2144: iconst_2
    //   2145: iushr
    //   2146: wide
    //   2150: ior
    //   2151: wide
    //   2155: wide
    //   2159: iconst_5
    //   2160: ishl
    //   2161: wide
    //   2165: wide
    //   2169: bipush 27
    //   2171: iushr
    //   2172: wide
    //   2176: wide
    //   2180: wide
    //   2184: ior
    //   2185: wide
    //   2189: wide
    //   2193: wide
    //   2197: ixor
    //   2198: wide
    //   2202: ixor
    //   2203: wide
    //   2207: wide
    //   2211: wide
    //   2215: iadd
    //   2216: wide
    //   2220: aload_0
    //   2221: getfield 20	com/tencent/token/core/encrypt/random/e:f	[I
    //   2224: wide
    //   2228: wide
    //   2232: iconst_1
    //   2233: iadd
    //   2234: wide
    //   2238: wide
    //   2242: wide
    //   2246: iaload
    //   2247: wide
    //   2251: wide
    //   2255: wide
    //   2259: iadd
    //   2260: ldc 56
    //   2262: iadd
    //   2263: wide
    //   2267: iload 247
    //   2269: wide
    //   2273: iadd
    //   2274: wide
    //   2278: wide
    //   2282: bipush 30
    //   2284: ishl
    //   2285: wide
    //   2289: wide
    //   2293: iconst_2
    //   2294: iushr
    //   2295: wide
    //   2299: ior
    //   2300: istore 23
    //   2302: wide
    //   2306: iconst_5
    //   2307: ishl
    //   2308: wide
    //   2312: wide
    //   2316: bipush 27
    //   2318: iushr
    //   2319: wide
    //   2323: wide
    //   2327: wide
    //   2331: ior
    //   2332: wide
    //   2336: wide
    //   2340: iload 23
    //   2342: ixor
    //   2343: wide
    //   2347: ixor
    //   2348: wide
    //   2352: wide
    //   2356: wide
    //   2360: iadd
    //   2361: wide
    //   2365: aload_0
    //   2366: getfield 20	com/tencent/token/core/encrypt/random/e:f	[I
    //   2369: wide
    //   2373: wide
    //   2377: iconst_1
    //   2378: iadd
    //   2379: wide
    //   2383: wide
    //   2387: wide
    //   2391: iaload
    //   2392: wide
    //   2396: wide
    //   2400: wide
    //   2404: iadd
    //   2405: ldc 56
    //   2407: iadd
    //   2408: wide
    //   2412: wide
    //   2416: wide
    //   2420: iadd
    //   2421: wide
    //   2425: wide
    //   2429: bipush 30
    //   2431: ishl
    //   2432: wide
    //   2436: wide
    //   2440: iconst_2
    //   2441: iushr
    //   2442: wide
    //   2446: ior
    //   2447: istore 28
    //   2449: iload_1
    //   2450: iconst_1
    //   2451: iadd
    //   2452: istore_1
    //   2453: wide
    //   2457: istore 26
    //   2459: wide
    //   2463: istore 32
    //   2465: wide
    //   2469: istore 29
    //   2471: wide
    //   2475: istore 31
    //   2477: goto -658 -> 1819
    //   2480: aload_0
    //   2481: getfield 28	com/tencent/token/core/encrypt/random/e:a	I
    //   2484: iload 26
    //   2486: iadd
    //   2487: wide
    //   2491: aload_0
    //   2492: wide
    //   2496: putfield 28	com/tencent/token/core/encrypt/random/e:a	I
    //   2499: aload_0
    //   2500: getfield 32	com/tencent/token/core/encrypt/random/e:b	I
    //   2503: iload 32
    //   2505: iadd
    //   2506: wide
    //   2510: aload_0
    //   2511: wide
    //   2515: putfield 32	com/tencent/token/core/encrypt/random/e:b	I
    //   2518: aload_0
    //   2519: getfield 34	com/tencent/token/core/encrypt/random/e:c	I
    //   2522: iload 28
    //   2524: iadd
    //   2525: wide
    //   2529: aload_0
    //   2530: wide
    //   2534: putfield 34	com/tencent/token/core/encrypt/random/e:c	I
    //   2537: aload_0
    //   2538: getfield 36	com/tencent/token/core/encrypt/random/e:d	I
    //   2541: iload 23
    //   2543: iadd
    //   2544: wide
    //   2548: aload_0
    //   2549: wide
    //   2553: putfield 36	com/tencent/token/core/encrypt/random/e:d	I
    //   2556: aload_0
    //   2557: getfield 38	com/tencent/token/core/encrypt/random/e:e	I
    //   2560: iload 29
    //   2562: iadd
    //   2563: wide
    //   2567: aload_0
    //   2568: wide
    //   2572: putfield 38	com/tencent/token/core/encrypt/random/e:e	I
    //   2575: aload_0
    //   2576: iconst_0
    //   2577: putfield 41	com/tencent/token/core/encrypt/random/e:g	I
    //   2580: iconst_0
    //   2581: istore_1
    //   2582: iload_1
    //   2583: bipush 16
    //   2585: if_icmpge +17 -> 2602
    //   2588: aload_0
    //   2589: getfield 20	com/tencent/token/core/encrypt/random/e:f	[I
    //   2592: iload_1
    //   2593: iconst_0
    //   2594: iastore
    //   2595: iload_1
    //   2596: iconst_1
    //   2597: iadd
    //   2598: istore_1
    //   2599: goto -17 -> 2582
    //   2602: return
  }
}

/* Location:           D:\dex2jar\classes.dex.dex2jar.jar
 * Qualified Name:     com.tencent.token.core.encrypt.random.e
 * JD-Core Version:    0.6.2
 */