/**
 * 
 */
package cc.aileron.template.compiler;

/**
 * @author aileron
 */
public interface TemplateInstruction
{
    /**
     * インストラクションの種類
     */
    enum Category
    {
        /**
         * コメント
         */
        COMMENT,

        /**
         * 分岐
         */
        DEF,

        /**
         * 反復
         */
        EACH,

        /**
         * 実行
         */
        EXECUTE,

        /**
         * インクルード
         */
        INCLUDE,

        /**
         * 順次
         */
        SEQUENTIAL,

        /**
         * 名前空間の束縛
         */
        WITH;
    }

    /**
     * @param child
     * @return procedure
     */
    TemplateProcedure procedure(Iterable<TemplateInstructionTree> child);
}
