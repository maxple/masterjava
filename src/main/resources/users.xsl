<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:x="http://javaops.ru">
    <xsl:template match="/">
        <html>
            <body>
                <h2>Users</h2>
                <table border="1">
                    <tr bgcolor="#9acd32">
                        <th style="text-align:left">Full name</th>
                        <th style="text-align:left">E-mail</th>
                    </tr>
                    <xsl:for-each select="x:Payload/x:Users/x:User">
                        <tr>
                            <td>
                                <xsl:value-of select="x:fullName"/>
                            </td>
                            <td>
                                <xsl:value-of select="@email"/>
                            </td>
                        </tr>
                    </xsl:for-each>
                </table>
            </body>
        </html>
    </xsl:template>
</xsl:stylesheet>
