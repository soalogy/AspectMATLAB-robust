package transformer.expr;

import ast.*;

public class CopyExprTransformer extends AbstractExprTransformer {
    @Override
    public ASTNode ASTNodeHandle(ASTNode operand) {
        return operand.copy();
    }

    @Override
    protected Expr caseRangeExpr(RangeExpr rangeExpr) {
        if (rangeExpr.hasIncr()) {
            Expr lowerExpr = this.transform(rangeExpr.getLower());
            Expr incrExpr  = this.transform(rangeExpr.getIncr());
            Expr upperExpr = this.transform(rangeExpr.getUpper());

            RangeExpr retExpr = (RangeExpr) ASTNodeHandle(rangeExpr);
            retExpr.setLower(lowerExpr);
            retExpr.setIncr(incrExpr);
            retExpr.setUpper(upperExpr);

            return retExpr;
        } else {
            Expr lowerExpr = this.transform(rangeExpr.getLower());
            Expr upperExpr = this.transform(rangeExpr.getUpper());

            RangeExpr retExpr = (RangeExpr) ASTNodeHandle(rangeExpr);
            retExpr.setLower(lowerExpr);
            retExpr.setUpper(upperExpr);

            return retExpr;
        }
    }

    @Override
    protected Expr caseColonExpr(ColonExpr colonExpr) {
        return (ColonExpr) ASTNodeHandle(colonExpr);
    }

    @Override
    protected Expr caseEndExpr(EndExpr endExpr) {
        return (EndExpr) ASTNodeHandle(endExpr);
    }

    @Override
    protected Expr caseFunctionHandleExpr(FunctionHandleExpr functionHandleExpr) {
        FunctionHandleExpr copiedExpr = (FunctionHandleExpr) ASTNodeHandle(functionHandleExpr);
        copiedExpr.setName(new Name(functionHandleExpr.getName().getID()));
        return copiedExpr;
    }

    @Override
    protected Expr caseLambdaExpr(LambdaExpr lambdaExpr) {
        CopyExprTransformer copyTransformer = new CopyExprTransformer();
        Expr copiedLambdaBody = copyTransformer.transform(lambdaExpr.getBody());

        LambdaExpr copiedExpr = (LambdaExpr) ASTNodeHandle(lambdaExpr);
        ast.List<Name> newInputPramList = new ast.List<>();
        copiedExpr.getInputParamList().stream().forEachOrdered(name -> newInputPramList.add(new Name(name.getID())));

        copiedExpr.setInputParamList(newInputPramList);
        copiedExpr.setBody(copiedLambdaBody);

        return copiedExpr;
    }

    @Override
    protected Expr caseCellArrayExpr(CellArrayExpr cellArrayExpr) {
        CellArrayExpr retExpr = (CellArrayExpr) ASTNodeHandle(cellArrayExpr);

        ast.List<Row> newCellArrayRowList = new ast.List<>();
        cellArrayExpr.getRowList().stream()
                .map(row -> {
                    Row copiedRow = (Row) ASTNodeHandle(row);
                    ast.List<Expr> newRowExprList = new ast.List<>();
                    row.getElementList().stream().map(this::transform).forEachOrdered(newRowExprList::add);
                    copiedRow.setElementList(newRowExprList);
                    return copiedRow;
                })
                .forEachOrdered(newCellArrayRowList::add);

        retExpr.setRowList(newCellArrayRowList);

        return retExpr;
    }

    @Override
    protected Expr caseSuperClassMethodExpr(SuperClassMethodExpr superClassMethodExpr) {
        SuperClassMethodExpr copiedExpr = (SuperClassMethodExpr) ASTNodeHandle(superClassMethodExpr);

        copiedExpr.setClassName(new Name(superClassMethodExpr.getClassName().getID()));
        copiedExpr.setFuncName(new Name(superClassMethodExpr.getFuncName().getID()));

        return copiedExpr;
    }

    @Override
    protected Expr caseNameExpr(NameExpr nameExpr) {
        NameExpr copiedExpr = (NameExpr) ASTNodeHandle(nameExpr);

        copiedExpr.setName(new Name(nameExpr.getName().getID()));

        return copiedExpr;
    }

    @Override
    protected Expr caseParameterizedExpr(ParameterizedExpr parameterizedExpr) {
        ParameterizedExpr retExpr = (ParameterizedExpr) ASTNodeHandle(parameterizedExpr);

        Expr copiedTargetExpr = this.transform(parameterizedExpr.getTarget());
        retExpr.setTarget(copiedTargetExpr);

        ast.List<Expr> newParameterList = new ast.List<>();
        parameterizedExpr.getArgList().stream()
                .map(this::transform)
                .forEachOrdered(newParameterList::add);
        retExpr.setArgList(newParameterList);

        return retExpr;
    }

    @Override
    protected Expr caseCellIndexExpr(CellIndexExpr cellIndexExpr) {
        CellIndexExpr retExpr = (CellIndexExpr) ASTNodeHandle(cellIndexExpr);

        Expr copiedTarget = this.transform(cellIndexExpr.getTarget());
        retExpr.setTarget(copiedTarget);

        ast.List<Expr> newParameterList = new ast.List<>();
        cellIndexExpr.getArgList().stream()
                .map(this::transform)
                .forEachOrdered(newParameterList::add);
        retExpr.setArgList(newParameterList);

        return retExpr;
    }

    @Override
    protected Expr caseDotExpr(DotExpr dotExpr) {
        DotExpr retExpr = (DotExpr) ASTNodeHandle(dotExpr);

        Expr copiedTarget = this.transform(dotExpr.getTarget());
        retExpr.setTarget(copiedTarget);

        return retExpr;
    }

    @Override
    protected Expr caseMatrixExpr(MatrixExpr matrixExpr) {
        MatrixExpr retExpr = (MatrixExpr) ASTNodeHandle(matrixExpr);


        ast.List<Row> newMatrixRowList = new ast.List<>();
        matrixExpr.getRowList().stream()
                .map(row -> {
                    Row copiedRow = (Row) ASTNodeHandle(row);
                    ast.List<Expr> newRowExprList = new ast.List<>();
                    row.getElementList().stream().map(this::transform).forEachOrdered(newRowExprList::add);
                    copiedRow.setElementList(newRowExprList);
                    return copiedRow;
                })
                .forEachOrdered(newMatrixRowList::add);
        retExpr.setRowList(newMatrixRowList);

        return retExpr;
    }

    @Override
    protected Expr caseIntLiteralExpr(IntLiteralExpr intLiteralExpr) {
        return intLiteralExpr.treeCopy();
    }

    @Override
    protected Expr caseFPLiteralExpr(FPLiteralExpr fpLiteralExpr) {
        return fpLiteralExpr.treeCopy();
    }

    @Override
    protected Expr caseStringLiteralExpr(StringLiteralExpr stringLiteralExpr) {
        return stringLiteralExpr.treeCopy();
    }

    private final UnaryExpr unaryOperatorDispatch(UnaryExpr unaryExpr) {
        UnaryExpr retExpr = (UnaryExpr) ASTNodeHandle(unaryExpr);
        Expr copiedOperand = this.transform(unaryExpr.getOperand());
        retExpr.setOperand(copiedOperand);
        return retExpr;
    }

    @Override
    protected Expr caseUMinusExpr(UMinusExpr uMinusExpr) {
        return unaryOperatorDispatch(uMinusExpr);
    }

    @Override
    protected Expr caseUPlusExpr(UPlusExpr uPlusExpr) {
        return unaryOperatorDispatch(uPlusExpr);
    }

    @Override
    protected Expr caseNotExpr(NotExpr notExpr) {
        return unaryOperatorDispatch(notExpr);
    }

    @Override
    protected Expr caseMTransposeExpr(MTransposeExpr mTransposeExpr) {
        return unaryOperatorDispatch(mTransposeExpr);
    }

    @Override
    protected Expr caseArrayTransposeExpr(ArrayTransposeExpr arrayTransposeExpr) {
        return unaryOperatorDispatch(arrayTransposeExpr);
    }

    private final BinaryExpr binaryOperatorDispatch(BinaryExpr binaryExpr) {
        BinaryExpr retExpr = (BinaryExpr) ASTNodeHandle(binaryExpr);
        Expr copiedLHS = this.transform(binaryExpr.getLHS());
        Expr copiedRHS = this.transform(binaryExpr.getRHS());
        retExpr.setLHS(copiedLHS);
        retExpr.setRHS(copiedRHS);
        return retExpr;
    }

    @Override
    protected Expr casePlusExpr(PlusExpr plusExpr) {
        return binaryOperatorDispatch(plusExpr);
    }

    @Override
    protected Expr caseMinusExpr(MinusExpr minusExpr) {
        return binaryOperatorDispatch(minusExpr);
    }

    @Override
    protected Expr caseMTimesExpr(MTimesExpr mTimesExpr) {
        return binaryOperatorDispatch(mTimesExpr);
    }

    @Override
    protected Expr caseMDivExpr(MDivExpr mDivExpr) {
        return binaryOperatorDispatch(mDivExpr);
    }

    @Override
    protected Expr caseMLDivExpr(MLDivExpr mlDivExpr) {
        return binaryOperatorDispatch(mlDivExpr);
    }

    @Override
    protected Expr caseMPowExpr(MPowExpr mPowExpr) {
        return binaryOperatorDispatch(mPowExpr);
    }

    @Override
    protected Expr caseETimesExpr(ETimesExpr eTimesExpr) {
        return binaryOperatorDispatch(eTimesExpr);
    }

    @Override
    protected Expr caseEDivExpr(EDivExpr eDivExpr) {
        return binaryOperatorDispatch(eDivExpr);
    }

    @Override
    protected Expr caseELDivExpr(ELDivExpr elDivExpr) {
        return binaryOperatorDispatch(elDivExpr);
    }

    @Override
    protected Expr caseEPowExpr(EPowExpr ePowExpr) {
        return binaryOperatorDispatch(ePowExpr);
    }

    @Override
    protected Expr caseAndExpr(AndExpr andExpr) {
        return binaryOperatorDispatch(andExpr);
    }

    @Override
    protected Expr caseOrExpr(OrExpr orExpr) {
        return binaryOperatorDispatch(orExpr);
    }

    @Override
    protected Expr caseShortCircuitAndExpr(ShortCircuitAndExpr shortCircuitAndExpr) {
        return binaryOperatorDispatch(shortCircuitAndExpr);
    }

    @Override
    protected Expr caseShortCircuitOrExpr(ShortCircuitOrExpr shortCircuitOrExpr) {
        return binaryOperatorDispatch(shortCircuitOrExpr);
    }

    @Override
    protected Expr caseLTExpr(LTExpr ltExpr) {
        return binaryOperatorDispatch(ltExpr);
    }

    @Override
    protected Expr caseGTExpr(GTExpr gtExpr) {
        return binaryOperatorDispatch(gtExpr);
    }

    @Override
    protected Expr caseLEExpr(LEExpr leExpr) {
        return binaryOperatorDispatch(leExpr);
    }

    @Override
    protected Expr caseGEExpr(GEExpr geExpr) {
        return binaryOperatorDispatch(geExpr);
    }

    @Override
    protected Expr caseEQExpr(EQExpr eqExpr) {
        return binaryOperatorDispatch(eqExpr);
    }

    @Override
    protected Expr caseNEExpr(NEExpr neExpr) {
        return binaryOperatorDispatch(neExpr);
    }
}
