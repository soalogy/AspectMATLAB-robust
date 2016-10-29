package transformer.patternExpand;

import ast.Expr;
import ast.PatternLoop;
import transformer.UnboundedIdentifier;

import java.util.Map;

public final class PatternLoopTrans extends PatternTrans {
    public PatternLoopTrans(PatternLoop patternLoop, Map<String, Expr> predefinedPattern) {
        super(patternLoop, predefinedPattern);
    }

    @Override
    public boolean hasFurtherTransform() {
        return false;
    }

    @Override
    public boolean hasTransformOnCurrentNode() {
        return false;
    }

    @Override
    public Expr copyAndTransform() throws UnboundedIdentifier {
        return pattern.treeCopy();
    }
}
