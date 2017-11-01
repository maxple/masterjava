<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:x="http://javaops.ru">
    <xsl:param name="projectId"/>
    <xsl:template match="/">
        <html>
            <body>
                <h2>Groups</h2>
                <table border="1">
                    <tr bgcolor="#9acd32">
                        <th style="text-align:left">ID</th>
                        <th style="text-align:left">Type</th>
                    </tr>
                    <xsl:for-each select="x:Payload/x:Projects/x:Project[@id=$projectId]/x:Groups/x:Group">
                        <tr>
                            <td>
                                <xsl:value-of select="@id"/>
                            </td>
                            <td>
                                <xsl:value-of select="@type"/>
                            </td>
                        </tr>
                    </xsl:for-each>
                </table>
            </body>
        </html>
    </xsl:template>
</xsl:stylesheet>
