/**
 * JDBCにて直接やり取りする
 * 
 * <ol>
 * <li>(insert/update/delete/fetch(count|one|list|list-paging))の何れかの
 * sql-templateを取得する</li>
 * <li>sql-templateを解析し、実行可能形式にする</li>
 * <li>データ取得ハンドラを渡し、データを取得する</li>
 * </ol>
 */
package cc.aileron.dao.jdbc;