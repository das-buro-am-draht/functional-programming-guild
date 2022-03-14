# React + Redux aus der FP-Perspektive

## React

### Klassen vs. Funtionskomponente

Eine Komponnete, die bei jedem Klick auf einen Button eine Zahl inkreementiert und anzeigt in Klassenschreibweise: 

```js
class Counter extends React.Component {
     constructor(props) {
       super(props);
       this.state = { count: 0 };
    }
    
    handleClick = () => {
       this.setState({ count: this.state.count + 1 });
     };

     render() {
       return (
         <div className="counter">
           <h1 className="count">{this.state.count}</h1>
    
           <button type="button" onClick={this.handleClick}>
             Increment
           </button>
         </div>
       );
     }
}

export default Counter;

```

... in Funktionsschreibweise

```js
const Counter = () => {
    const [count, setCount] = useState(0);

    const incrementCount = () => {
        setCount(count + 1);
    };

    return (
        <div className="counter">
            <h1 className="count">{count}</h1>

            <button type="button" onClick={incrementCount}>
                Increment
            </button>
        </div>
    );
};

export default Counter;
```

### Seiteneffekte in React 

React dient der Kommunikation mit dem/der Nutzer/in. Es ist also Teil der Kommunilation mit der Außenwelt. Daher lassen sich Seiteneffekte nicht komplett vermeiden. Es ist jedoch möglich, Komponenten in pure und nicht zu unterteilen und den puren Anteil des Codes weiter zu erhöhen.

```js
// Wir unterteilen die Komponente in zwei Komponenten. 
// Eine mit Seiteneffekten und eine pure

const Counter = () => {
    // useState ist nicht pur, weil bei dem gleichen Parameter 
    // unterschiedliche Werte zurückkommen können
    const [count, setCount] = useState(0);
    const incrementCount = () => {
        setCount(count + 1);
    };

    return <CounterPure count={count} onIncrement={incrementCount}>;
}

const CounterPure = (props) => {
    const {count, onIncrement} = props;

    return (
        <div className="counter">
            <h1 className="count">{count}</h1>

            <button type="button" onClick={onIncrement}>
                Increment
            </button>
        </div>
    );
}

```

Diese Technik kann gerade bei komplexeren Komponenten das Testen vereinfachen.

## React vs direkte DOM-Manipulation

Wenn mit der DOM direkt gerbeitet wird, Seiteffekte fast in jeder Zeile. Es ist schwierig diesen Code zu testen. Durch die Nutzung einer virtuellen DOM (einer reinen Datenstruktur), erteilen React-Koponneten Aufträge an React. Die Manipulation der DOM wird geschiet indirekt. Dadurch reduziert React die Anzahl an Seiteneffekten erheblich. Die Vituelle DOM können inspiziert und mity erwarteten Werten verglichen werden. Das Testen wir dadurch deutlich einfacher.

## React-Redux

Zur Anbindung der React-Komponenten an Redux wird die [react-redux](https://react-redux.js.org/) Bibliothek verwendet. react-redux stellt [`connect`](https://react-redux.js.org/api/connect)-Funktion bereit, um die Anbindung an den Redux-Store zu ermöglichen.

```js

// mapt den Redux-State auf die propeties der Komponente
const mapStateToProps = state => ({count: state.count});

// mapt dei von den Komponneten empfangenen Events (idR.) auf Actions.
// onIncerement wird in die Proprties der Komponente eingeführt
const mapStateToProps = dispatch => ({
    onIncrement: () => dispatch({ type: 'INCREMENT' })
});

// CounterPure ist dabei die selbe Komponente wie schon oben
const Counter = connect(mapStateToProps, mapStateToProps)(CounterPure);

```

Die `connect`-Funktion begünstigt eine Trennung zwischen puren Komponenten und der Anbindug an Redux. Die neueren [`useSelector` und `useDispatch` Hooks](https://react-redux.js.org/api/hooks) erlauben eine Mischung.


## Redux

[Selektoren](https://redux.js.org/usage/deriving-data-selectors) und [Reducer](https://redux.js.org/tutorials/essentials/part-1-overview-concepts#reducers) sind immer pur. [Middleware](https://redux.js.org/understanding/history-and-design/middleware) hingegen sind idR. nicht pur. `store.getState`, `store.dispatch` und die `next` Funktionen sind nicht pur.

## Redux-observable
[redux-observable](https://redux-observable.js.org/) bietet eine Möglichkeit, um Redux-Middleware mit Streams aufzubauen. Diese Middleware werden [Epic](https://redux-observable.js.org/docs/basics/Epics.html) genannt. Jede Action kommt in den Stream rein, wird im Stream verabreitet und ausgegeben. Die ausgebene Action wird dann von redux-observable dispatcht. Daher werden `store.dispatch` und `next` nicht benötigt. [Auf den letzten Stand des States kann durch die `withLatestFrom` Funktion von RxJs zugegriffen werden.](https://redux-observable.js.org/docs/basics/Epics.html#accessing-the-stores-state) Dardurch wird eine weitere Quelle für Seiteneffekte umgangen. 