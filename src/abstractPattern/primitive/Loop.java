package abstractPattern.primitive;

import Matlab.Utils.IReport;
import Matlab.Utils.Report;
import abstractPattern.utils.LoopType;
import ast.Name;
import ast.PatternLoop;

import java.util.Optional;

/** an abstract representation on the loop pattern */
public final class Loop extends Primitive {
    private final LoopType loopType;
    private final String identifier;

    /**
     * construct from {@link PatternLoop} AST node. If the loop pattern do not provide a loop type signature, the
     * pattern will automatically resolve it as a star[*] wildcard.
     * @param patternLoop {@link PatternLoop} AST node
     * @param enclosingFilename enclosing aspect file path
     * @throws IllegalArgumentException if {@code patternLoop} do not have a loop name signature
     */
    @Deprecated
    @SuppressWarnings("deprecation")
    public Loop(PatternLoop patternLoop, String enclosingFilename) {
        super(patternLoop, enclosingFilename);

        assert originalPattern instanceof PatternLoop;
        identifier = Optional
                .ofNullable(((PatternLoop) originalPattern).getIdentifier())
                .orElseThrow(IllegalArgumentException::new)
                .getID();
        loopType = LoopType.fromString(Optional
                .ofNullable(((PatternLoop) originalPattern).getType())
                .orElse(new Name("*"))
                .getID());
    }

    /**
     * perform structural weeding on the loop pattern, it will:
     * <ul>
     *     <li>raise error if the pattern type signature use {@code [..]} wildcard,</li>
     *     <li>raise error if the pattern name signature use {@code [..]} wildcard</li>
     * </ul>
     * @return the structural weeding report
     */
    @Override
    public IReport getStructureValidationReport() {
        IReport report = new Report();
        assert originalPattern instanceof PatternLoop;
        if ("..".equals(((PatternLoop) originalPattern).getType().getID())) {
            report.AddError(
                    enclosingFilename,
                    startLineNumber, startColumnNumber,
                    "wildcard [..] is not a valid matcher in loop pattern for loop type, use [*] instead"
            );
        }
        if ("..".equals(identifier)) {
            report.AddError(
                    enclosingFilename,
                    startLineNumber, startColumnNumber,
                    "wildcard [..] is not a valid matcher in loop pattern for loop name, use [*] instead"
            );
        }
        return report;
    }

    @Override
    public String toString() {
        return getModifierToString(String.format("loop(%s:%s)", loopType.toString(), identifier));
    }
}
