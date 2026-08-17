# PDFBox (Android fork)
-keep class com.tom_roush.pdfbox.** { *; }
-keep class com.tom_roush.fontbox.** { *; }
-keep class com.tom_roush.harmony.** { *; }
-dontwarn com.tom_roush.pdfbox.**
-dontwarn com.tom_roush.fontbox.**

# Apache POI / OOXML
-keep class org.apache.poi.** { *; }
-keep class org.apache.xmlbeans.** { *; }
-keep class org.openxmlformats.** { *; }
-keep class schemasMicrosoftComOfficeOffice.** { *; }
-keep class schemaorg_apache_xmlbeans.** { *; }
-dontwarn org.apache.poi.**
-dontwarn org.apache.xmlbeans.**
-dontwarn org.openxmlformats.**
-dontwarn org.apache.commons.compress.**
-dontwarn org.apache.batik.**
-dontwarn org.apache.log4j.**
-dontwarn org.apache.logging.log4j.**
-dontwarn org.apache.commons.logging.**
-dontwarn org.osgi.**
-dontwarn javax.xml.**
-dontwarn org.w3c.dom.**
-dontwarn org.etsi.uri.**
-dontwarn com.microsoft.schemas.**
-dontwarn com.graphbuilder.**
