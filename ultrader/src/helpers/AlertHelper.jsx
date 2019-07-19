import Swal from "sweetalert2";
import withReactContent from "sweetalert2-react-content";

const MySwal = withReactContent(Swal);

export function alertSuccess(msg) {
  MySwal.fire({
    title: "Success",
    text: msg,
    type: "success",
    timer: 2000
  });
}

export function alertError(msg) {
  MySwal.fire({
    title: "Error",
    text: msg,
    type: "error",
    timer: 5000
  });
}
